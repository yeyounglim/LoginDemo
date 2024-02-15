package demo.logindemo.config;

import demo.logindemo.filter.AuthoritiesLoggingAfterFilter;
import demo.logindemo.filter.CsrfCookieFilter;
import demo.logindemo.filter.JWTTokenGeneratorFilter;
import demo.logindemo.filter.JWTTokenValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        //기본 필터
        //CsrfTokenHandler 구현하기 csrf토큰 활성화.
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                // jwt토큰을 쓸것이기 때문에 sessionID 같은걸 쓰지 않기로함
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
                // CORS 이슈 허용
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); // 프론트 허용포트
                                config.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드를 허용
                                config.setAllowCredentials(true); // 쿠키나 인증 헤더와 같은 크레덴셜을 요청과 함께 전송할 수 있도록
                                config.setAllowedHeaders(Collections.singletonList("*")); // 요청 헤더로 모든 값을 허용
                                // jwt토큰을 UI로 보내야 함. 설정안해주면 토큰을 주고받을수 없음. Authorization이라는 리스폰스 헤더를 사용.
                                // csrf토큰은 프레임워크가 제공한 헤더이기 때문에 설정 안해줘도 됨.
                                config.setExposedHeaders(Arrays.asList("Authorization","Refresh-Token")); //Refresh token은 셀프작명인데 적절한 헤더명이 있는지 찾아보기..
                                config.setMaxAge(3600L); // 브라우저가 CORS pre-flight 요청의 결과를 캐시하는 시간으로 3600초(1시간)을 지정
                                return config;
                            }
                }))
             //   .csrf(AbstractHttpConfigurer::disable) // 시큐리티 기본 csrf보안. 잠시 해제해줌
                .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/welcome", "/register")// 공공 api는 통과해야됨. post요청들
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // 쿠키이름으로 생성하고 쿠키값을 읽을수 있게 해주는 설정
                // 커스텀 필터
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class) // BasicAuthenticationFilter에서 자격 검증 및 엔드유저 실제 인증이 발생함
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class) // 인증 후 로그 찍는 필터
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class) //jwt토큰 생성 필터
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class) // 시큐리티의 실제 인증 유효성 검사 이전에 토큰 검사를 하고싶음

                // 접근 권한 설정
                .authorizeHttpRequests((requests)->requests
                        .requestMatchers("/account").hasAnyRole("USER","ADMIN")
                        .requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")
                        .requestMatchers("/user").authenticated()
                        .anyRequest().permitAll() //나머지 주소는 모든 권한이 허용되어 있음
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults()); // 클라이언트가 요청 헤더에 사용자 이름과 비밀번호를 Base64 인코딩 형태로 포함시켜 서버에 전송하는 방식.
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

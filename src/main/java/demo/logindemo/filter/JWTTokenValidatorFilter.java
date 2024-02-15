package demo.logindemo.filter;

import demo.logindemo.constants.SecurityConstants;
import demo.logindemo.model.Users;
import demo.logindemo.repository.UserRepository;
import demo.logindemo.util.JWTTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public class JWTTokenValidatorFilter extends OncePerRequestFilter { // jwt token유효성 검사

    private UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Spring ApplicationContext에서 CustomerRepository 빈을 수동으로 주입
        if (userRepository == null) {
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            userRepository = webApplicationContext.getBean(UserRepository.class);
        }
        //
        JWTTokenUtil gen = new JWTTokenUtil();
        // 클라이언트가 보낸 요청 안에 있는 헤더에서 토큰을 가져오기
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);

        if (null != jwt) {
            try {// access토큰이 정상이면
                // 시크릿 키 재생성
                SecretKey key = gen.getSecretKey(SecurityConstants.JWT_KEY);
                // 토큰 파싱하기
                Claims claims = gen.parseToken(key, jwt);
                // 파싱한 토큰 에서 정보 가져오기(클레임)
                String username = String.valueOf(claims.get("username"));
                String authorities = (String) claims.get("authorities");
                // UsernamePasswordAuthenticationToken타입의 인증 객체 생성해주기
                // 직접 객체를 생성하는 이유:시큐리티 프레임워크에게 jwt토큰 유효화 했고 문제가 없다는것을 알림. 그럼 시큐리티가 인증 성공이란걸 알아챔
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                // 위의 이유로 인증 객체를 SecurityContextHolder에 넣어야함
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {// access토큰이 만료되면
                // 헤더에 있는 리프레시 토큰 가져오기
                String refreshJwtToken = request.getHeader(SecurityConstants.REFRESH_JWT_HEADER);

                if (null != refreshJwtToken) {// 리프레시 토큰 확인하기
                    Date accTokenDate = new Date((new Date()).getTime() + 30000);

                    try {
                        // 리프레시 토큰 파싱하기
                        SecretKey key = gen.getSecretKey(SecurityConstants.REFRESH_JWT_KEY);
                        Claims claims = gen.parseToken(key, refreshJwtToken);

                        String username = String.valueOf(claims.get("username"));
                        String authorities = (String) claims.get("authorities");

                        Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        // 리프레시 토큰을 db에서 조회해서 현재 리프레시 토큰이랑 같은지 확인해야됨
                        List<Users> users = userRepository.findByEmail(auth.getName());

                        if (users.size() > 0) {
                            String getRefresh = users.get(0).getRefreshToken();
                            if (getRefresh.equals(refreshJwtToken)) {
                                // 토큰이 일치하면 jwt access토큰 재생성해서 헤더에 넣어줌
                                key = gen.getSecretKey(SecurityConstants.JWT_KEY);
                                String refreshAccessJwtToken = gen.generateToken(auth, key, accTokenDate);
                                response.setHeader(SecurityConstants.JWT_HEADER, refreshAccessJwtToken);
                            }
                        }
                    } catch (Exception exception) {
                        throw new BadCredentialsException("Invalid refresh Token !!");
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        //login중을 제외한 모든 api호출시 실행되고 싶음 해당경로일때는 실행 안됨
        return request.getServletPath().equals("/user");
    }
}

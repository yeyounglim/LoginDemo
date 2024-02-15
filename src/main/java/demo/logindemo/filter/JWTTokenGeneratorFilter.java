package demo.logindemo.filter;

import demo.logindemo.constants.SecurityConstants;
import demo.logindemo.model.Users;
import demo.logindemo.repository.UserRepository;
import demo.logindemo.util.JWTTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    private UserRepository userRepository; // 디비에 넣는 좋은방법 있는지 ......
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Spring ApplicationContext에서 CustomerRepository 빈을 수동으로 주입. GenericFilterBean으로 해결 가능한지 찾아보기..?
        if (userRepository == null) {
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            userRepository = webApplicationContext.getBean(UserRepository.class);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            Date accTokenDate = new Date((new Date()).getTime()+30000); // 테스트용 30초유효
            Date refreshTokenDate  = new Date((new Date()).getTime()+300000);

            JWTTokenUtil gen = new JWTTokenUtil();

            // key생성하기
            SecretKey key = gen.getSecretKey(SecurityConstants.JWT_KEY);
            // 생성한 키로 토큰 만들기
            String jwt = gen.generateToken(authentication, key, accTokenDate);

            // refresh토큰 만들기
            key = gen.getSecretKey(SecurityConstants.REFRESH_JWT_KEY);
            String refreshJwt = gen.generateToken(authentication, key, refreshTokenDate);

            // header에다 토큰 셋팅해주기
            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
            response.setHeader(SecurityConstants.REFRESH_JWT_HEADER, refreshJwt);

            String name = authentication.getName();
            Users users = userRepository.findByEmail(name).get(0);

            // 해당 고객이 존재하는 경우 리프레시 토큰을 업데이트
            if (users != null) {
                users.setRefreshToken(refreshJwt);
                userRepository.save(users);
            } else {
                throw new RuntimeException("Customer not found");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        // /user요청 시에만 필터 실행하고 나머지는 이 필터(토큰생성필터)를 실행안한다는뜻
        return !request.getServletPath().equals("/user");
    }
}

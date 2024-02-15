package demo.logindemo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter { // 시큐리티 필터 extends

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request에 있는 csrf토큰 읽기
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(null != csrfToken.getHeaderName()){ // null이 아니면 프레임워크가 csrf토큰을 생성햇단 뜻
            response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        }//헤더만 보내고 쿠키는 보내지 않음
        filterChain.doFilter(request, response);
    }
}

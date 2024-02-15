package demo.logindemo.filter;

import jakarta.servlet.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.logging.Logger;

public class AuthoritiesLoggingAfterFilter implements Filter { // 인증 끝나면 로그 뿌리는 필터.

    private final Logger LOG = Logger.getLogger(AuthoritiesLoggingAfterFilter.class.getName());
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            LOG.info("User " + authentication.getName() + " is successfully authenticated and "
                    + "has the authorities " + authentication.getAuthorities().toString());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

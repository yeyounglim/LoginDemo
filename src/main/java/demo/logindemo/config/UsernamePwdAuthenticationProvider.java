package demo.logindemo.config;

import demo.logindemo.model.Authority;
import demo.logindemo.model.Users;
import demo.logindemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // 패스워드 암호화

    // 원하는 종류의 유저 인증 사항을 작성한다.
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 유저이름 pwd 불러오기
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        List<Users> users = userRepository.findByEmail(username); // 이메일로 유저정보 불러오기
        if (users.size() > 0) {
            if (passwordEncoder.matches(pwd, users.get(0).getPwd())) {// 비번 맞는지 체크
                return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthorities(users.get(0).getAuthorities())); // 토큰 형태로 인증 객체 만들기. 권한이 부여되면 수정불가
            } else {
                throw new BadCredentialsException("Invalid password!");
            }
        }else {// 유저가 회원가입 안하면
            throw new BadCredentialsException("No user registered with this details!");
        }
    }

    private List<GrantedAuthority> getGrantedAuthorities(Set<Authority> authorities) { // 엔티티에서 실제 권한 열 이름을 읽음
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName())); // 읽어서 SimpleGrantedAuthority 객체생성
        }
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) { // 인증에 대한 유저이름, 비번 스타일 전달하고싶음
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication); //ProviderMangager가 AuthenticationProvider내에 supports 메소드를 주입시키려 함
    }
}

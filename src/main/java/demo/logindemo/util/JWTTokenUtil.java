package demo.logindemo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JWTTokenUtil {
    public String generateToken(Authentication authentication, SecretKey key, Date expDate){
        String jwt = Jwts.builder()
                .issuer("Login")
                .subject("JWT Token") // 아무거나 넣어도 됨
                .claim("username", authentication.getName()) // 로그인된 유저의 정보를 채울수 있음
                .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                .issuedAt(new Date()) // 발행 날짜
                .expiration(expDate) // 만료 날짜 설정하기
                .signWith(key) // 토큰에 디지털 서명 하기
                .compact();
        return "Bearer " + jwt;

    }
    public Claims parseToken(SecretKey key, String jwt){
        jwt = jwt.substring(7);
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)  // 여기서 일치 여부를 판별함. 불일치시 exception
                .getPayload();// 이후로 서명값은 읽어오지 않음
        return claims;
    }


    /*
    * 모든 권한 읽어오기 권한이 쉼표로 구분됨
    * */
    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for(GrantedAuthority authority : collection){
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }

    //키생성 메서드
    public SecretKey getSecretKey(String genKey) {
        return Keys.hmacShaKeyFor(genKey.getBytes(StandardCharsets.UTF_8));
    }

}

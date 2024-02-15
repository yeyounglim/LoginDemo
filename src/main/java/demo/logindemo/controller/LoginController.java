package demo.logindemo.controller;

import demo.logindemo.model.Users;
import demo.logindemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행한다.
// 로그인 진행이 완료가되면 시큐리티 세션을 만들어준다.(Security ContextHolder 키값에다 세션저장)
// 시큐리티 세션에 들어갈수 있는 오브젝트가 정해져 있음 => Authentication 타입 객체
// Authentication안에  User 정보가 있어야 한다.

@RestController
public class LoginController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // 패스워드 암호화

    /*
    * 회원가입
    * */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Users users) {
        Users savedUsers = null;
        ResponseEntity response = null;
        try {
            //비번 암호화
            String hashPwd = passwordEncoder.encode(users.getPwd());
            users.setPwd(hashPwd);
            users.setCreateDt(new Date(System.currentTimeMillis()));
            savedUsers = userRepository.save(users); // 저장
            if (savedUsers.getId() > 0) {// 저장됐으면 pk를 얻었겠지.. 0보다 크면 성공
                response = ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("Given user details are successfully registered");
            }
        } catch (Exception ex) {
            response = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occured due to " + ex.getMessage());
        }
        return response;
    }

    /*
    * 엔드유저가 로그인 클릭하면
    * */
    @RequestMapping("/user")
    public Users getUserDetailsAfterLogin(Authentication authentication) {
        //Authentication 객체 에서 로그인된 유저의 이름을 알아내고 이메일을 알아낸다. Authentication-시큐리티에서 자동제공
        List<Users> users = userRepository.findByEmail(authentication.getName());
        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }
}

package demo.logindemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@SpringBootApplication
//@EnableWebSecurity(debug = true) // 디버깅용 필터 테스트용. 현재 시큐리티버전 버그있음..
public class LoginDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginDemoApplication.class, args);
    }

}

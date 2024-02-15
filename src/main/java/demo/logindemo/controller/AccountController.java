package demo.logindemo.controller;

import demo.logindemo.model.Users;
import demo.logindemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/account")
    public String getAccountDetails(@RequestParam int id) {
        List<Users> byEmail = userRepository.findById(id);
        if (byEmail != null ) {
            return byEmail.get(0).getEmail();
        }else {
            return null;
        }
    }
}

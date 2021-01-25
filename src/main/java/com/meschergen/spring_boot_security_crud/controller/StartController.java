package com.meschergen.spring_boot_security_crud.controller;

import com.meschergen.spring_boot_security_crud.model.User;
import com.meschergen.spring_boot_security_crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 12.01.2021
 *
 * @author MescheRGen
 */
@RestController
@RequestMapping("/welcome")
public class StartController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<String> printWelcome(Principal principal) {
        StringBuilder sb = new StringBuilder();
        if(principal != null) {
            User user = userRepository.getByUsername(principal.getName());
            sb.append(String.format("Hello, %s!<br>",user.getFirstName()));
            //model.addAttribute("userId", user.getId()); // for debug access
        } else {
            sb.append("Hello, anonymous!<br>");
        }

        sb.append("Im RESTful, SECURE and FANCY! :)<br>");
        sb.append("Im Running by Spring Boot<br>");

        //return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
        return ResponseEntity.ok(sb.toString());
    }
}

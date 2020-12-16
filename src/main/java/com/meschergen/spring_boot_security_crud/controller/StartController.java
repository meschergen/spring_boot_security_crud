package com.meschergen.spring_boot_security_crud.controller;

import com.meschergen.spring_boot_security_crud.model.User;
import com.meschergen.spring_boot_security_crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */
@Controller
public class StartController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String printWelcome(ModelMap model, Principal principal) {
        List<String> messages = new ArrayList<>();
        if(principal != null) {
            User user = userRepository.getByUsername(principal.getName());
            messages.add(String.format("Hello, %s!",user.getFirstName()));
            model.addAttribute("userId", user.getId());
        } else {
            messages.add("Hello, anonymous!");
        }

        messages.add("Im OK, and SECURE! :)");
        messages.add("And Running by Spring Boot");
        model.addAttribute("messages", messages);
        return "index";
    }
}

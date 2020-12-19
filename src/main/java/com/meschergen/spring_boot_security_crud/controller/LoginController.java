package com.meschergen.spring_boot_security_crud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 18.12.2020
 *
 * @author MescheRGen
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping()
    public String login() {
        return "login";
    }

}

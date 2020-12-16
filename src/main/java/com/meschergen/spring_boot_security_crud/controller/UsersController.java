package com.meschergen.spring_boot_security_crud.controller;

import com.meschergen.spring_boot_security_crud.model.User;
import com.meschergen.spring_boot_security_crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */

@Controller
@RequestMapping("/users")
public class UsersController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public String index(ModelMap model){
        model.addAttribute("userList", userRepository.findAll());
        return "users/list";
    }

    @GetMapping("/{id}")
    public String userInfo(@PathVariable("id") long id, ModelMap model){
        model.addAttribute("user", userRepository.getOne(id));
        return "users/info";
    }

    @GetMapping("/new")
    public String addUser(@ModelAttribute("user") User user) {
        return "users/new";
    }

    @GetMapping("/{id}/edit")
    public String editUserInfo(@PathVariable("id") long id, ModelMap model){
        model.addAttribute("user", userRepository.getOne(id));
        return "users/edit";
    }

    @GetMapping("/registration")
    public String userRegistration(@ModelAttribute("user") User user) {
        return "users/registration";
    }

    @PostMapping("/**")
    public String insertIntoDatabase(@ModelAttribute("user") @Valid User user,
                                     BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            if (principal != null) {
                return "users/new";
            } else {
                return "users/registration";
            }
        }

        userRepository.save(user);
        if (principal != null) {
            return "redirect:/users";
        }
        return "redirect:/";
    }

    @PatchMapping("/{id}")
    public String updateUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @PathVariable("id") long id) { // просто убрать id?

        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        userRepository.save(user);
        return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") long id){
        userRepository.deleteById(id);
        return "redirect:/users";
    }


}

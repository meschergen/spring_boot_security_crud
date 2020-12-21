package com.meschergen.spring_boot_security_crud.controller;

import com.meschergen.spring_boot_security_crud.model.User;
import com.meschergen.spring_boot_security_crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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

    /*Resource(name = "curUser")
    private User currentUser;*/

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public String list(ModelMap model, Principal principal){

        if(principal != null) {
            User user = userRepository.getByUsername(principal.getName());
            model.addAttribute("userId", user.getId());
        }
        model.addAttribute("userList", userRepository.findAll());
        return "users/list";
    }
    //@PreAuthorize("hasRole('ADMIN') OR authentication.name == #u.findById(#id).get().getUsername()") //@P("u")UserRepository ur
    //@PreAuthorize("hasRole('ADMIN') OR #id == #user.getId()") //@CurrentUser User user
    //@PreAuthorize("hasRole('ADMIN') OR #id == #user.getId()")
    //@PreAuthorize("hasAnyRole('ADMIN, USER')")
    @PreAuthorize("hasRole('ADMIN') OR #id == #user.getId()")
    @GetMapping("/{id}")
    public String userInfo(@PathVariable("id") long id, ModelMap model,User user){
        model.addAttribute("userId", id);
        model.addAttribute("user", userRepository.getOne(id));
        return "users/info";
    }

    @Bean(name = "curUser")
    @Lazy
    public User getCurrentUser() {
        return userRepository.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /*@Target({ ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @AuthenticationPrincipal
    public @interface CurrentUser {
    }*/

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String addUser(@ModelAttribute("user") User user) {
        return "users/new";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editUserInfo(@PathVariable("id") long id, ModelMap model){
        model.addAttribute("user", userRepository.getOne(id));
        return "users/edit";
    }

    @PreAuthorize("isAnonymous()")
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

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") long id){
        userRepository.deleteById(id);
        return "redirect:/users";
    }


}

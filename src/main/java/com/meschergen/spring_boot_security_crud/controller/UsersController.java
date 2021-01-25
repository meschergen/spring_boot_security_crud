package com.meschergen.spring_boot_security_crud.controller;

import com.meschergen.spring_boot_security_crud.model.Role;
import com.meschergen.spring_boot_security_crud.model.User;
import com.meschergen.spring_boot_security_crud.repository.RoleRepository;
import com.meschergen.spring_boot_security_crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 10.01.2021
 *
 * @author MescheRGen
 */

@RestController
@RequestMapping("/users")
public class UsersController {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


   /* @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"", "/list"})
    public String list(@ModelAttribute("user") User user, ModelMap model, Principal principal){

        /*if(principal != null) {
            User userPr = userRepository.getByUsername(principal.getName());
            model.addAttribute("userId", userPr.getId());
        }
        model.addAttribute("roleList", roleRepository.findAll());
        model.addAttribute("userList", userRepository.findAll());
        return "users/list";
    }*/

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"", "/list"})
    public ModelAndView list(ModelAndView modelAndView, @ModelAttribute("user") User user) {
        modelAndView.setViewName("/users/list");
        return modelAndView;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"/getAllUsers"})
    public ResponseEntity<List<User>> list(){
        return ResponseEntity.ok(userRepository.findAll());
    }


    @PreAuthorize("hasAnyRole('ADMIN, USER')")
    @GetMapping("/{id}")
    public ModelAndView userInfo(@PathVariable("id") long id, ModelAndView modelAndView, Principal principal){
        User user = userRepository.getByUsername(principal.getName());

        if(AuthorityUtils.authorityListToSet(SecurityContextHolder
                                                .getContext()
                                                .getAuthentication()
                                                .getAuthorities())
                                                .contains("ROLE_ADMIN") || user.getId() == id) {

            modelAndView.addObject("userId", id);
            modelAndView.addObject("user", userRepository.getOne(id));
            modelAndView.setViewName("/users/info");
            return modelAndView;
        } else {
            throw new AccessDeniedException("403 returned");
        }
    }

    /*@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String addUser(@ModelAttribute("user") User user) {
        return "users/new";
    }*/

    /*@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editUserInfo(@PathVariable("id") long id, ModelMap model){
        model.addAttribute("user", userRepository.getOne(id));
        return "users/list";
    }*/

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit")
    public ResponseEntity<User> editUserModal(Long id) {
        return ResponseEntity.ok(userRepository.findById(id).get());
    }


    @PreAuthorize("isAnonymous()")
    @GetMapping("/registration")
    public ModelAndView userRegistration(@ModelAttribute("user") User user, ModelAndView modelAndView) {
        modelAndView.setViewName("users/registration");
        return modelAndView;
    }

    @PostMapping("/**")
    public String insertIntoDatabase(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                                     @RequestParam(value = "selectedRoles", required = false) Long[] selectedRoles,
                                     Principal principal,
                                     ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            if (principal != null) {
                //return "users/new";
                //return "users/list";
                modelAndView.setViewName("users/list");

            } else {
                //return "users/registration";
                modelAndView.setViewName("users/registration");
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // хеширование введённого пароля
        Set<Role> roles = new HashSet<>();

        if (selectedRoles != null) {
            for (Long roleId : selectedRoles) {
                roles.add(roleRepository.getOne(roleId));
            }
        } else { // COSTYL если не выделенно ни одной роли всё равно добаляем роль пользователя
            roles.add(roleRepository.getOne(2L)); // ROLE_USER
        }
        user.setRoles(roles);

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

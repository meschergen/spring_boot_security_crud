package com.meschergen.spring_boot_security_crud.controller;

import com.meschergen.spring_boot_security_crud.model.Role;
import com.meschergen.spring_boot_security_crud.model.User;
import com.meschergen.spring_boot_security_crud.repository.RoleRepository;
import com.meschergen.spring_boot_security_crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * 10.01.2021
 *
 * @author MescheRGen
 */

@Controller
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


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"", "/list"})
    public String list(@ModelAttribute("user") User user, ModelMap model, Principal principal){

        if(principal != null) {
            User userPr = userRepository.getByUsername(principal.getName());
            model.addAttribute("userId", userPr.getId());
        }
        model.addAttribute("roleList", roleRepository.findAll());
        model.addAttribute("userList", userRepository.findAll());
        return "users/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN, USER')")
    @GetMapping("/{id}")
    public String userInfo(@PathVariable("id") long id, ModelMap model, Principal principal){
        User user = userRepository.getByUsername(principal.getName());

        if(AuthorityUtils.authorityListToSet(SecurityContextHolder
                                                .getContext()
                                                .getAuthentication()
                                                .getAuthorities())
                                                .contains("ROLE_ADMIN") || user.getId() == id) {

            model.addAttribute("userId", id);
            model.addAttribute("user", userRepository.getOne(id));
            return "users/info";
        } else {
            throw new AccessDeniedException("403 returned");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String addUser(@ModelAttribute("user") User user) {
        return "users/new";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editUserInfo(@PathVariable("id") long id, ModelMap model){
        model.addAttribute("user", userRepository.getOne(id));
        return "users/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit")
    @ResponseBody
    public User editUserModal(Long id) {
        return userRepository.getOne(id);
    }


    @PreAuthorize("isAnonymous()")
    @GetMapping("/registration")
    public String userRegistration(@ModelAttribute("user") User user) {
        return "users/registration";
    }

    @PostMapping("/**")
    public String insertIntoDatabase(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                                     @RequestParam(value = "selectedRoles", required = false) Long[] selectedRoles,
                                      Principal principal) {

        if (bindingResult.hasErrors()) {
            if (principal != null) {
                //return "users/new";
                return "users/list";
            } else {
                return "users/registration";
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

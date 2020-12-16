package com.meschergen.spring_boot_security_crud.config.handler;

import com.meschergen.spring_boot_security_crud.model.User;
import com.meschergen.spring_boot_security_crud.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private UserDetailsServiceImp userDetailsService; // для возможности вытащить user.id, через username
    // чтобы сформировать с помощью id ссылку на страницу пользователя

    @Autowired
    public void setUserDetailsService(UserDetailsServiceImp userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        User user =  userDetailsService.getByUsername(authentication.getName());
        if (roles.contains("ROLE_ADMIN")) {
            httpServletResponse.sendRedirect("/users"); // /admin
        } else if (roles.contains("ROLE_USER")) {
            httpServletResponse.sendRedirect("users/" + user.getId());
        } else {
            httpServletResponse.sendRedirect("/");
        }
    }
}

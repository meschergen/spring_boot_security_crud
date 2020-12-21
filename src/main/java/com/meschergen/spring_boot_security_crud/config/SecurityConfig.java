package com.meschergen.spring_boot_security_crud.config;

import com.meschergen.spring_boot_security_crud.config.handler.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    @Qualifier("userDetailsServiceImp")
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setLoginSuccessHandler(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/login")
                .successHandler(loginSuccessHandler)
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll();

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
                .csrf().disable(); // выключение кроссдоменной секьюрности

        http.authorizeRequests()
                //.antMatchers("/login").anonymous()
                .antMatchers("/users").hasRole("ADMIN")
                .antMatchers("/users/registration").anonymous()
                .antMatchers("/users/info/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/users/edit").hasRole("ADMIN")
                .antMatchers("/users/list").hasRole("ADMIN")
                .antMatchers("users/new").hasRole("ADMIN")
                .antMatchers("/").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setPasswordEncoder(passwordEncoder());
        daoProvider.setUserDetailsService(userDetailsService);
        return daoProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}


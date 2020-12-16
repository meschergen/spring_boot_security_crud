package com.meschergen.spring_boot_security_crud.service;

import com.meschergen.spring_boot_security_crud.model.User;
import com.meschergen.spring_boot_security_crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 16.12.2020
 *
 * @author MescheRGen
 */
@Service
public class UserDetailsServiceImp implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getByUsername(String username){
        return userRepository.getByUsername(username);
    }

    @Override
    @Transactional // из-за Lazy Init
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s is not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }
}

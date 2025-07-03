package com.invoice.Invoice_System.security;

import com.invoice.Invoice_System.model.User;
import com.invoice.Invoice_System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired

    private UserRepository userRepository;

    @Override

    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User not found in DB");
            throw new UsernameNotFoundException("User not found");
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            throw new RuntimeException("Role is missing in DB for user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(

                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().trim()))
        );
    }
}

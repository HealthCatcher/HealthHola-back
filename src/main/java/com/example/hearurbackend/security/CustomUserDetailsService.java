package com.example.hearurbackend.security;

import com.example.hearurbackend.domain.user.entity.User;
import com.example.hearurbackend.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userData = userRepository.findById(username).orElse(null);
        if (userData != null) {
            return new CustomUserDetails(userData);
        }
        System.out.println("User not found with username: " + username);
        return null;
    }
}
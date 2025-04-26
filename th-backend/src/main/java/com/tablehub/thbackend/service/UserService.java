package com.tablehub.thbackend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.model.User;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> matches(password, user.getPassword()));
    }

    private boolean matches(String rawPassword, String encodedPassword) {
        // Secure password matching using PasswordEncoder
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

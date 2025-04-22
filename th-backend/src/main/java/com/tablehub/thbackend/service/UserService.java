package com.tablehub.thbackend.service;

import org.springframework.stereotype.Service;
import java.util.Optional;

import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.model.User;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> matches(password, user.getPassword()));
    }
}

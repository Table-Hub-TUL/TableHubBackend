package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.model.AppUser;
import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.security.auth.UserPrinciple;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsPasswordService; // <--- Import this
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, UserDetailsPasswordService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserPrinciple.build(user);
    }

    @Override
    @Transactional
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        // 1. Find the existing entity
        AppUser appUser = userRepository.findByUserName(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Update the password (it assumes newPassword is already encoded)
        appUser.setPassword(newPassword);

        // 3. Save to DB
        userRepository.save(appUser);

        // 4. Return updated UserDetails (refreshed from the updated entity)
        return UserPrinciple.build(appUser);
    }
}
package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.model.AppUser;
import com.tablehub.thbackend.model.Role;
import com.tablehub.thbackend.model.RoleName;
import com.tablehub.thbackend.repo.RoleRepository;
import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.service.interfaces.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void assignRole(String username, RoleName role) {
        AppUser user = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User with this username " + username + " does not exist"));
        Role roleToAssign = roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Set<Role> userRoles = user.getRoles();
        userRoles.add(roleToAssign);

        userRepository.save(user);

    }
}

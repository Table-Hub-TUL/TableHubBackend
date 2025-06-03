package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.response.AuthJwtResponse;
import com.tablehub.thbackend.dto.request.AuthLoginRequest;
import com.tablehub.thbackend.dto.response.AuthMessageResponse;
import com.tablehub.thbackend.dto.request.AuthSignUpRequest;
import com.tablehub.thbackend.model.AppUser;
import com.tablehub.thbackend.model.Role;
import com.tablehub.thbackend.model.RoleName;
import com.tablehub.thbackend.repo.RoleRepository;
import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.service.implementations.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtProvider;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthLoginRequest loginRequest) {
        Authentication authentication = daoAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new AuthJwtResponse(jwt,userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthSignUpRequest signUpRequest) {

        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new AuthMessageResponse("Fail -> Username is already taken."), HttpStatus.BAD_REQUEST);
        }

        // Create user account
        AppUser user = new AppUser();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getNickname());
        user.setUserName(signUpRequest.getUsername());
       // user.setPoints(0);
        user.setRegisteredAt(OffsetDateTime.now());
        userRepository.save(user);

        return new ResponseEntity<>(new AuthMessageResponse("User registered successfully."), HttpStatus.OK);

    }
}

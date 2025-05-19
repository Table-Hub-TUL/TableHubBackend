package com.tablehub.thbackend.security.auth;

import com.tablehub.thbackend.dto.auth.JwtResponse;
import com.tablehub.thbackend.dto.auth.LoginForm;
import com.tablehub.thbackend.dto.auth.ResponseMessage;
import com.tablehub.thbackend.dto.auth.SignUpForm;
import com.tablehub.thbackend.model.AppUser;
import com.tablehub.thbackend.model.Role;
import com.tablehub.thbackend.model.RoleName;
import com.tablehub.thbackend.repo.RoleRepo;
import com.tablehub.thbackend.repo.UserRepo;
import com.tablehub.thbackend.security.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtProvider;

    @Autowired
    public AuthController(DaoAuthenticationProvider daoAuthenticationProvider,
                          UserRepo userRepository, RoleRepo roleRepository,
                          PasswordEncoder passwordEncoder, JwtService jwtProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {
        Authentication authentication = daoAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken."), HttpStatus.BAD_REQUEST);
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
        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage("User registered successfully."), HttpStatus.OK);

    }
}

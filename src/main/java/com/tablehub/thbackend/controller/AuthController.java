package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.ChangePasswordRequest;
import com.tablehub.thbackend.dto.request.TokenRefreshRequest;
import com.tablehub.thbackend.dto.response.AuthJwtResponse;
import com.tablehub.thbackend.dto.request.AuthLoginRequest;
import com.tablehub.thbackend.dto.response.AuthMessageResponse;
import com.tablehub.thbackend.dto.request.AuthSignUpRequest;
import com.tablehub.thbackend.exception.TokenRefreshException;
import com.tablehub.thbackend.model.AppUser;
import com.tablehub.thbackend.model.RefreshToken;
import com.tablehub.thbackend.model.Role;
import com.tablehub.thbackend.model.RoleName;
import com.tablehub.thbackend.repo.RoleRepository;
import com.tablehub.thbackend.repo.UserRepository;
import com.tablehub.thbackend.security.auth.UserPrinciple;
import com.tablehub.thbackend.service.implementations.JwtService;
import com.tablehub.thbackend.service.implementations.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
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
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    private final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsPasswordService passwordService;

    @Operation(
            summary = "Sign in user",
            description = "Authenticates a user and returns a JWT token for authorization"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthJwtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid input",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid
                                              @RequestBody
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User login details", required = true, content = @Content(schema = @Schema(implementation = AuthLoginRequest.class)))
                                              AuthLoginRequest loginRequest) {
        log.info("Processing authentication request for user '{}'", loginRequest.getUsername());

        try {
            Authentication authentication = daoAuthenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateJwtToken(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            log.info("User '{}' authenticated successfully. Issuing JWT.", userDetails.getUsername());
            AppUser appUser = userRepository.findByUserName(userDetails.getUsername()).orElseThrow();

            // Generate Refresh Token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(appUser.getId());

            AuthJwtResponse responseBody = new AuthJwtResponse(jwt, refreshToken.getToken(), userDetails.getUsername(), userDetails.getAuthorities());
            return ResponseEntity.ok(responseBody);

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user '{}': {}", loginRequest.getUsername(), e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid credentials");
        }
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new JWT access token and refresh token using a valid refresh token. The provided refresh token will be rotated (invalidated) and a new one will be issued for enhanced security."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully refreshed tokens",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthJwtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Refresh token expired or invalid",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid input",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshtoken(@Valid
                                          @RequestBody
                                          @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Refresh token request", required = true, content = @Content(schema = @Schema(implementation = TokenRefreshRequest.class)))
                                          TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Create new Access Token
                    String token = jwtProvider.generateToken(UserPrinciple.build(user));

                    // Rotate Refresh Token (Optional but recommended security practice)
                    // This deletes the used refresh token and creates a new one
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

                    return ResponseEntity.ok(new AuthJwtResponse(
                            token,
                            newRefreshToken.getToken(),
                            user.getUserName(),
                            UserPrinciple.build(user).getAuthorities()
                    ));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @Operation(
            summary = "Change password",
            description = "Changes the password for the currently authenticated user. Requires the current password for verification before setting the new password."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthMessageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - User not authenticated or current password incorrect",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid input",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid
                                            @RequestBody
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Password change request with current and new password", required = true, content = @Content(schema = @Schema(implementation = ChangePasswordRequest.class)))
                                            ChangePasswordRequest request) {

        // 1. Get the current authenticated UserDetails principal
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 2. Check if the old password matches the one in UserDetails (which contains the DB hash)
        if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Current password does not match.");
        }

        // 3. Encode the new password
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        // 4. Update using the standard service interface
        passwordService.updatePassword(userDetails, encodedNewPassword);

        return ResponseEntity.ok(new AuthMessageResponse("Password changed successfully!"));
    }


    @PostMapping("/logout")
    @Operation(summary = "Logout User", description = "Revokes the user's refresh token, effectively logging them out.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log out successful"),
            @ApiResponse(responseCode = "403", description = "Refresh token invalid or not found")
    })
    public ResponseEntity<?> logoutUser(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(token -> {
                    refreshTokenService.deleteByUserId(token.getUser().getId());
                    return ResponseEntity.ok(new AuthMessageResponse("Log out successful!"));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with ROLE_USER privileges"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthMessageResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"User registered successfully.\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Username already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthMessageResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Fail -> Username is already taken.\"}")
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid
                                          @RequestBody
                                          @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                  description = "User registration details",
                                                  required = true,
                                                  content = @Content(schema = @Schema(implementation = AuthSignUpRequest.class))
                                          )
                                          AuthSignUpRequest signUpRequest) {
        log.info("Processing registration request for username '{}' and email '{}'",
                signUpRequest.getUsername(), signUpRequest.getEmail());

        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            log.warn("Registration failed: Username '{}' is already taken.", signUpRequest.getUsername());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthMessageResponse("Error: Username is already taken."));
        }

        try {
            AppUser user = new AppUser();
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("CRITICAL: Default 'ROLE_USER' not found in the database."));

            user.setUserName(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setName(signUpRequest.getNickname());

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            user.setPoints(0);
            user.setRegisteredAt(OffsetDateTime.now());

            userRepository.save(user);
            log.info("User '{}' registered successfully.", user.getUserName());
            return ResponseEntity.ok(new AuthMessageResponse("User registered successfully."));
        } catch (RuntimeException e) {
            log.error("Failed to register user '{}' due to a server error.", signUpRequest.getUsername(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthMessageResponse("Error: Could not register user due to an internal error."));
        }
    }
}


package com.tablehub.thbackend.config;

import com.tablehub.thbackend.security.jwt.JwtAuthEntryPoint;
import com.tablehub.thbackend.security.jwt.JwtAuthTokenFilter;
import com.tablehub.thbackend.service.implementations.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthEntryPoint unauthorizedHandler;

    // --- Your beans are all correct ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }

    /**
     * Runs FIRST (Order 1): Secures your STATELESS API.
     * This filter chain now ONLY matches /api/** routes.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("/api/**")) // <-- SPECIFIC match
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // All API requests must be authenticated
                )
                .exceptionHandling(unauthorized -> unauthorized
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // STATELESS
                );

        // Add your JWT filter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Runs SECOND (Order 2): Secures your STATEFUL Vaadin UI.
     * This filter chain now matches EVERYTHING ELSE (/**).
     */
    @Bean
    @Order(2)
    public SecurityFilterChain vaadinUiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // <-- CATCH-ALL
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Permit all the public Vaadin routes you listed
                        .requestMatchers(
                                "/",
                                "/?v-r=init&**",
                                "/favicon.ico",
                                "/offline-stub.html",
                                "/VAADIN/**",
                                "/vaadinServlet/**",
                                "/frontend/**",
                                "/sw.js",
                                "/offline.html",
                                "/icons/**",
                                "/images/**",
                                "/styles/**"
                        ).permitAll()

                        // Permit your other public routes
                        .requestMatchers(
                                "/h2-console/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/auth/**",
                                "/login"
                        ).permitAll()

                        // Secure your admin UI
                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")

                        // Block any other unauthenticated requests
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/admin/restaurants", true)
                )
                .sessionManagement(session -> session
                        // STATEFUL: Vaadin needs this
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .headers(headers -> headers
                        // Fix for H2 console
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );

        return http.build();
    }
}
package com.tablehub.thbackend.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.tablehub.thbackend.security.auth.UserPrinciple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime; // in milliseconds

    public String generateToken(UserDetails userDetails) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plusMillis(expirationTime)))
                .withClaim("roles", userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .sign(algorithm);
    }

    public String generateJwtToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationTime*1000))
                .claim("roles", userPrinciple.getAuthorities().stream().map(Objects::toString).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserNameFromJwtToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }

    public List<String> extractRoles(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getClaim("roles")
                .asList(String.class);
    }

}

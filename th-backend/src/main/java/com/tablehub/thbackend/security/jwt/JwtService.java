package com.tablehub.thbackend.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
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

    public String generateToken(String username, Map<String, Object> claims) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTCreator.Builder builder = JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plusMillis(expirationTime)));
        claims.forEach((key, value) -> {
            if (value instanceof Boolean) {
                builder.withClaim(key, (Boolean) value);
            } else if (value instanceof Integer) {
                builder.withClaim(key, (Integer) value);
            }
        });
        return builder.sign(algorithm);
    }
}

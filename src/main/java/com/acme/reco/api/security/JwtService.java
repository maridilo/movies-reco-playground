package com.acme.reco.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;  // <-- IMPORTANTE
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;            // <-- SecretKey (no Key)
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.issuer:acme}") String issuer,
            @Value("${jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        // secretBase64 debe ser >= 256 bits (32 bytes) cuando se decodifica
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key, Jwts.SIG.HS256)       // <-- HS256 con SecretKey
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)                      // <-- verifyWith(SecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            boolean notExpired = claims.getExpiration() == null
                    || claims.getExpiration().after(new Date());

            return notExpired && user.getUsername().equals(claims.getSubject());
        } catch (Exception e) {
            return false;
        }
    }
}

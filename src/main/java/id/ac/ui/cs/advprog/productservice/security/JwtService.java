package id.ac.ui.cs.advprog.productservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import jakarta.annotation.PostConstruct;
import java.util.Base64;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String rawSecret;

    private Key secretKey;
    private JwtParser parser;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(rawSecret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
        parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
    }

    public String extractUsername(String token) {
        return parser.parseClaimsJws(token).getBody().getSubject();
    }

    public List<String> extractRoles(String token) {
        return parser.parseClaimsJws(token).getBody().get("roles", List.class);
    }

    public boolean isTokenValid(String token) {
        try {
            return !parser.parseClaimsJws(token).getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
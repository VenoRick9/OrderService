package by.baraznov.orderservice.util;

import by.baraznov.orderservice.model.Order;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtilTest {
    private final SecretKey jwtAccessSecret;

    public JwtUtilTest() {

        String testSecret = "test-secret-key-for-jwt-signing-in-tests-1234567890";
        this.jwtAccessSecret = Keys.hmacShaKeyFor(testSecret.getBytes());
    }

    public String generateToken(Order order) {
        Instant expirationInstant = LocalDateTime.now()
                .plusMinutes(10)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        
        return Jwts.builder()
                .setSubject(String.valueOf(order.getUserId()))
                .setExpiration(Date.from(expirationInstant))
                .signWith(jwtAccessSecret)
                .compact();
    }

}
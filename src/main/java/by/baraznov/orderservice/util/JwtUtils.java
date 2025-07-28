package by.baraznov.orderservice.util;


import by.baraznov.orderservice.model.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims, String token) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setLogin(claims.get("login", String.class));
        jwtInfoToken.setUserId(Integer.valueOf(claims.getSubject()));
        jwtInfoToken.setToken(token);
        return jwtInfoToken;
    }


}

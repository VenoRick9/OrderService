package by.baraznov.orderservice.util;

import by.baraznov.orderservice.model.JwtAuthentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {

    public Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof JwtAuthentication jwt) {
            return jwt.getUserId();
        }
        throw new AccessDeniedException("User is not authenticated");
    }
}
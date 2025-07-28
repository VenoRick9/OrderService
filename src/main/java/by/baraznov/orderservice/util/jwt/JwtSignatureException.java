package by.baraznov.orderservice.util.jwt;

public class JwtSignatureException extends RuntimeException {
    public JwtSignatureException(String message) {
        super(message);
    }
}

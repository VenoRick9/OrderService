package by.baraznov.orderservice.util.feign;

public class FeignClientBadRequestException extends RuntimeException {
    public FeignClientBadRequestException(String message) {
        super(message);
    }
}

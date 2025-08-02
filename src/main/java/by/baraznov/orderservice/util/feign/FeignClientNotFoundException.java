package by.baraznov.orderservice.util.feign;

public class FeignClientNotFoundException extends RuntimeException {
    public FeignClientNotFoundException(String message) {
        super(message);
    }
}

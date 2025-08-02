package by.baraznov.orderservice.util.feign;

public class FeignException extends RuntimeException {
  public FeignException(String message) {
    super(message);
  }
}

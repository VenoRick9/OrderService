package by.baraznov.orderservice.util;

public class ItemAlreadyExist extends RuntimeException {
    public ItemAlreadyExist(String message) {
        super(message);
    }
}

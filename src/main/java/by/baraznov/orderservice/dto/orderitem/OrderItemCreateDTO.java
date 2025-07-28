package by.baraznov.orderservice.dto.orderitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemCreateDTO(
        @NotNull(message = "Item ID is required")
        @Positive(message = "Item ID must be a positive number")
        Integer itemId,
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Integer quantity
) {
}

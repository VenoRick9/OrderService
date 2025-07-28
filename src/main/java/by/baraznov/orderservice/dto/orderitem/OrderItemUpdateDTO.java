package by.baraznov.orderservice.dto.orderitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemUpdateDTO(
        @NotNull
        Integer id,
        @Positive(message = "Item ID must be a positive number")
        Integer itemId,
        @Positive(message = "Quantity must be greater than 0")
        Integer quantity
) {
}

package by.baraznov.orderservice.dto.item;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ItemCreateDTO(
        @NotBlank(message = "Item name must not be blank")
        @Size(min = 2, max = 60, message = "Item name must be between 2 and 60 characters")
        String name,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Price must be positive or zero")
        @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits and 2 decimal places")
        BigDecimal price
) {
}

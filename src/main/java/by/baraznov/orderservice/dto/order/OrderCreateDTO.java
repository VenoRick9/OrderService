package by.baraznov.orderservice.dto.order;

import by.baraznov.orderservice.dto.orderitem.OrderItemCreateDTO;
import by.baraznov.orderservice.model.OrderStatus;
import by.baraznov.orderservice.util.EnumValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateDTO(
        @EnumValidator(enumClass = OrderStatus.class)
        @NotNull(message = "Order status is required")
        String status,
        @NotEmpty(message = "Order must contain at least one item")
        List<@Valid OrderItemCreateDTO> orderItems
) {
}

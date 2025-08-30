package by.baraznov.orderservice.dto.order;

import by.baraznov.orderservice.dto.orderitem.OrderItemCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderCreateDTO(
        @NotEmpty(message = "Order must contain at least one item")
        List<@Valid OrderItemCreateDTO> orderItems
) {
}

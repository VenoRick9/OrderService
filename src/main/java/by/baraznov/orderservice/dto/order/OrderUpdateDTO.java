package by.baraznov.orderservice.dto.order;

import by.baraznov.orderservice.dto.orderitem.OrderItemUpdateDTO;
import jakarta.validation.Valid;

import java.util.List;

public record OrderUpdateDTO(
        List<@Valid OrderItemUpdateDTO>orderItems
) {
}

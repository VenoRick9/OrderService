package by.baraznov.orderservice.dto.order;

import by.baraznov.orderservice.dto.orderitem.OrderItemUpdateDTO;
import by.baraznov.orderservice.model.OrderStatus;
import by.baraznov.orderservice.util.EnumValidator;
import jakarta.validation.Valid;

import java.util.List;

public record OrderUpdateDTO(
        @EnumValidator(enumClass = OrderStatus.class)
        String status,
        List<@Valid OrderItemUpdateDTO>orderItems
) {
}

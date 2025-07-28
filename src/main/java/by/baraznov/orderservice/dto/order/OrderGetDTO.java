package by.baraznov.orderservice.dto.order;

import by.baraznov.orderservice.dto.orderitem.OrderItemGetDTO;
import by.baraznov.orderservice.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderGetDTO(
        Integer id,
        OrderStatus status,
        LocalDateTime creationDate,
        List<OrderItemGetDTO> orderItems
) {
}

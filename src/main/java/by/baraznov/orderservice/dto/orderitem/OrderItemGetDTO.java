package by.baraznov.orderservice.dto.orderitem;

import java.math.BigDecimal;

public record OrderItemGetDTO(
        Integer id,
        Integer itemId,
        String itemName,
        BigDecimal itemPrice,
        Integer quantity
) {
}

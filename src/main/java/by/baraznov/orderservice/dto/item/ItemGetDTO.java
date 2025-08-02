package by.baraznov.orderservice.dto.item;

import java.math.BigDecimal;

public record ItemGetDTO(
        Integer id,
        String name,
        BigDecimal price
) {
}

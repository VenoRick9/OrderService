package by.baraznov.orderservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderKafkaDTO(
        UUID userId,
        Integer orderId,
        BigDecimal paymentAmount
) implements Serializable {
}

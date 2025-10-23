package by.baraznov.orderservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record OrderKafkaDTO(
        Integer userId,
        Integer orderId,
        BigDecimal paymentAmount
) implements Serializable {
}

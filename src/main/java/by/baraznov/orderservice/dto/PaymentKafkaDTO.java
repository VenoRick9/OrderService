package by.baraznov.orderservice.dto;

public record PaymentKafkaDTO(
        String id,
        Integer orderId,
        String status
) {
}

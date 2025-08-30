package by.baraznov.orderservice.kafka;

import by.baraznov.orderservice.dto.PaymentKafkaDTO;
import by.baraznov.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaConsumer {
    private final OrderService orderService;
    @KafkaListener(topics = "CREATE_PAYMENT",groupId = "group2",containerFactory = "kafkaListenerContainerFactory")
    public void listenCreatePayment(PaymentKafkaDTO message) {
        System.out.println("Received Message: " + message);
        orderService.changeStatus(message);
    }

}

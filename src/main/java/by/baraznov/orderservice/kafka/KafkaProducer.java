package by.baraznov.orderservice.kafka;

import by.baraznov.orderservice.dto.OrderKafkaDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, OrderKafkaDTO> template;
    private static final String TOPIC = "CREATE_ORDER";
    public void sendCreateOrder(OrderKafkaDTO message) {
        this.template.send(TOPIC, message);
    }
}

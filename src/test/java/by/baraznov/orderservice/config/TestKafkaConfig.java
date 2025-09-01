package by.baraznov.orderservice.config;

import by.baraznov.orderservice.dto.OrderKafkaDTO;
import by.baraznov.orderservice.kafka.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@Configuration
public class TestKafkaConfig {

    @Bean
    @Primary
    public KafkaProducer kafkaProducer() {
        KafkaProducer mock = mock(KafkaProducer.class);
        doNothing().when(mock).sendCreateOrder(any(OrderKafkaDTO.class));
        return mock;
    }
}

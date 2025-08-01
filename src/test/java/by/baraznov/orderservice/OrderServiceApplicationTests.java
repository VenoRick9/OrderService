package by.baraznov.orderservice;

import by.baraznov.orderservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Import(TestContainersConfig.class)
@Testcontainers
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}

package by.baraznov.orderservice;

import by.baraznov.orderservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Import(TestContainersConfig.class)
@TestPropertySource(properties = "external.server.baseUrl=http://localhost:9120")
@Testcontainers
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}

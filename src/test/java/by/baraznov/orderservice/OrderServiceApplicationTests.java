package by.baraznov.orderservice;

import by.baraznov.orderservice.config.TestContainersConfig;
import by.baraznov.orderservice.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@TestPropertySource(properties = "external.server.baseUrl=http://localhost:9120")
@Testcontainers
class OrderServiceApplicationTests {
    @MockBean
    private JwtUtils jwtUtils;
    @Test
    void contextLoads() {
    }

}

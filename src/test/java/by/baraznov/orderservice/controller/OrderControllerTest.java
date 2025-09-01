package by.baraznov.orderservice.controller;

import by.baraznov.orderservice.config.TestContainersConfig;
import by.baraznov.orderservice.config.TestKafkaConfig;
import by.baraznov.orderservice.dto.OrderKafkaDTO;
import by.baraznov.orderservice.kafka.KafkaProducer;
import by.baraznov.orderservice.model.Item;
import by.baraznov.orderservice.model.Order;
import by.baraznov.orderservice.model.OrderStatus;
import by.baraznov.orderservice.repository.ItemRepository;
import by.baraznov.orderservice.repository.OrderRepository;
import by.baraznov.orderservice.util.JwtUtilTest;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 9120)
@TestPropertySource(properties = "external.server.baseUrl=http://localhost:9120")
@Import({TestContainersConfig.class, TestKafkaConfig.class})
@ImportAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtUtilTest testJwtUtil;
    @Autowired
    private KafkaProducer kafkaProducer;

    private String token;

    private Order order1, order2;
    private Item item1;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE items RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE orders RESTART IDENTITY CASCADE");
        order1 = Order.builder()
                .status(OrderStatus.NEW)
                .creationDate(LocalDateTime.now())
                .userId(1)
                .build();
        order2 = Order.builder()
                .status(OrderStatus.SUCCESS)
                .creationDate(LocalDateTime.now())
                .userId(2)
                .build();

        orderRepository.saveAll(List.of(order1, order2));
        item1 = Item.builder()
                .name("Iphone 14")
                .price(new BigDecimal("1199.99"))
                .build();
        itemRepository.save(item1);
        token = testJwtUtil.generateToken(order1);
    }

    @BeforeEach
    void setupStub() {
        stubFor(WireMock.get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                      "id": 1,
                                      "name": "Lev",
                                      "surname": "Lamar",
                                      "email": "lamar@example.com"
                                    }
                                """)));
        stubFor(WireMock.get(urlEqualTo("/users/2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                      "id": 2,
                                      "name": "Tiger",
                                      "surname": "Frank",
                                      "email": "frank@example.com"
                                    }
                                """)));
        doNothing().when(kafkaProducer).sendCreateOrder(any(OrderKafkaDTO.class));
    }

    @Test
    void test_getAllOrders() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].status").value("NEW"))
                .andExpect(jsonPath("$.content[1].status").value("SUCCESS"))
                .andExpect(jsonPath("$.content[0].user.name").value("Lev"))
                .andExpect(jsonPath("$.content[1].user.surname").value("Frank"));
        verify(getRequestedFor(urlEqualTo("/users/1")));
        verify(getRequestedFor(urlEqualTo("/users/2")));
    }

    @Test
    void test_getOrderById() throws Exception {
        mockMvc.perform(get("/orders/{id}", order1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.user.name").value("Lev"))
                .andExpect(jsonPath("$.user.surname").value("Lamar"));
        verify(getRequestedFor(urlEqualTo("/users/1")));
    }

    @Test
    void test_getOrderByIds() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("ids", String.valueOf(order1.getId()), String.valueOf(order2.getId()))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status").value("NEW"))
                .andExpect(jsonPath("$[1].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].user.name").value("Lev"))
                .andExpect(jsonPath("$[1].user.surname").value("Frank"));
        verify(getRequestedFor(urlEqualTo("/users/1")));
        verify(getRequestedFor(urlEqualTo("/users/2")));
    }

    @Test
    void test_getOrdersByStatus() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("status", "NEW")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status").value("NEW"))
                .andExpect(jsonPath("$.content[0].user.name").value("Lev"));
        verify(getRequestedFor(urlEqualTo("/users/1")));
    }

    @Test
    public void test_createOrder() throws Exception {
        String json = """
                    {
                        "orderItems":[
                            {
                                "itemId":"1",
                                "quantity":"2"
                            }
                        ]
                    }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.id").value("3"));
        verify(getRequestedFor(urlEqualTo("/users/1")));
    }

    @Test
    public void test_updateOrder() throws Exception {
        String json = """
                    {
                     "orderItems":[
                          {
                              "id":"1",
                              "itemId":"1",
                              "quantity":"6"
                          }
                      ]
                    }
                """;

        mockMvc.perform(patch("/orders/{id}", order1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        verify(getRequestedFor(urlEqualTo("/users/1")));
    }

    @Test
    public void test_deleteOrder() throws Exception {
        mockMvc.perform(delete("/orders/{id}", order2.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertFalse(orderRepository.findById(order2.getId()).isPresent());
    }
}
package by.baraznov.orderservice.controller;

import by.baraznov.orderservice.config.TestContainersConfig;
import by.baraznov.orderservice.model.Item;
import by.baraznov.orderservice.model.Order;
import by.baraznov.orderservice.model.OrderStatus;
import by.baraznov.orderservice.repository.ItemRepository;
import by.baraznov.orderservice.repository.OrderRepository;
import by.baraznov.orderservice.util.JwtUtilTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@TestPropertySource(properties = "external.server.baseUrl=http://localhost:9120")
class ItemControllerTest {
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

    private String token;

    private Order order;
    private Item item1, item2;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE items RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE orders RESTART IDENTITY CASCADE");
        order = Order.builder()
                .status(OrderStatus.NEW)
                .creationDate(LocalDateTime.now())
                .userId(1)
                .build();
        orderRepository.save(order);
        item1 = Item.builder()
                .name("Iphone 14")
                .price(new BigDecimal("1199.99"))
                .build();
        item2 = Item.builder()
                .name("Iphone 15")
                .price(new BigDecimal("1299.99"))
                .build();
        itemRepository.saveAll(List.of(item1, item2));
        token = testJwtUtil.generateToken(order);
    }

    @Test
    void test_getAllItems() throws Exception {
        mockMvc.perform(get("/items")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Iphone 14"))
                .andExpect(jsonPath("$.content[1].name").value("Iphone 15"));
    }

    @Test
    void test_getItemById() throws Exception {
        mockMvc.perform(get("/items/{id}", item1.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iphone 14"))
                .andExpect(jsonPath("$.price").value(new BigDecimal("1199.99")));
    }

    @Test
    void test_getItemByName() throws Exception {
        mockMvc.perform(get("/items")
                        .param("name", item1.getName())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iphone 14"))
                .andExpect(jsonPath("$.price").value(new BigDecimal("1199.99")));
    }

    @Test
    void test_createItem() throws Exception {
        String json = """
                    {
                      "name": "Iphone 22",
                      "price": "2099.99"
                    }
                """;

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Iphone 22"))
                .andExpect(jsonPath("$.price").value("2099.99"));
    }

    @Test
    void test_updateItem() throws Exception {
        String json = """
                    {
                      "name": "Iphone 2",
                      "price": "99.99"
                    }
                """;

        mockMvc.perform(patch("/items/{id}", item1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iphone 2"))
                .andExpect(jsonPath("$.price").value("99.99"));
    }

    @Test
    void test_updateItemName() throws Exception {
        String json = """
                    {
                      "name": "Iphone 34"
                    }
                """;

        mockMvc.perform(patch("/items/{id}", item1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iphone 34"))
                .andExpect(jsonPath("$.price").value("1199.99"));
    }

    @Test
    void test_deleteItem() throws Exception {
        mockMvc.perform(delete("/items/{id}", item2.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertFalse(itemRepository.findById(item2.getId()).isPresent());
    }
}
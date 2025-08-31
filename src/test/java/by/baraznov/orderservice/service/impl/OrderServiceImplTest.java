package by.baraznov.orderservice.service.impl;

import by.baraznov.orderservice.client.UserClient;
import by.baraznov.orderservice.dto.CardGetDTO;
import by.baraznov.orderservice.dto.OrderKafkaDTO;
import by.baraznov.orderservice.dto.UserGetDTO;
import by.baraznov.orderservice.dto.order.OrderCreateDTO;
import by.baraznov.orderservice.dto.order.OrderGetDTO;
import by.baraznov.orderservice.dto.order.OrderUpdateDTO;
import by.baraznov.orderservice.dto.orderitem.OrderItemCreateDTO;
import by.baraznov.orderservice.dto.orderitem.OrderItemGetDTO;
import by.baraznov.orderservice.kafka.KafkaProducer;
import by.baraznov.orderservice.mapper.order.OrderCreateDTOMapper;
import by.baraznov.orderservice.mapper.order.OrderGetDTOMapper;
import by.baraznov.orderservice.mapper.order.OrderUpdateDTOMapper;
import by.baraznov.orderservice.model.Item;
import by.baraznov.orderservice.model.Order;
import by.baraznov.orderservice.model.OrderItem;
import by.baraznov.orderservice.model.OrderStatus;
import by.baraznov.orderservice.repository.ItemRepository;
import by.baraznov.orderservice.repository.OrderRepository;
import by.baraznov.orderservice.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private OrderUpdateDTOMapper orderUpdateDTOMapper;
    @Mock
    private OrderGetDTOMapper orderGetDTOMapper;
    @Mock
    private OrderCreateDTOMapper orderCreateDTOMapper;
    @Mock
    private UserClient userClient;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private KafkaProducer kafkaProducer;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void test_getOrderById() {
        OrderItem orderItem1 = new OrderItem();
        OrderItemGetDTO orderItemGetDTO1 = new OrderItemGetDTO(1, 1, "Iphone 14",
                new BigDecimal("1199.99"), 2);
        UserGetDTO userGetDTO = new UserGetDTO(1, "John",
                "Diamond", LocalDate.of(2000, 1, 1),
                "Mail@gmail.com", List.of(new CardGetDTO(1, 1, "1234567891018121",
                "JOHN DIAMOND", LocalDate.of(2027, 1, 1))));
        Order order = new Order(1, 1, OrderStatus.NEW, LocalDateTime.now(), List.of(orderItem1));
        OrderGetDTO orderItemGetDTO = new OrderGetDTO(1, OrderStatus.NEW,
                LocalDateTime.now(), List.of(orderItemGetDTO1), userGetDTO);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderGetDTOMapper.toDto(order)).thenReturn(orderItemGetDTO);
        when(userClient.getUserById(1)).thenReturn(userGetDTO);

        OrderGetDTO result = orderService.getOrderById(1);

        assertEquals(orderItemGetDTO, result);
        verify(orderRepository).findById(1);
        verify(orderGetDTOMapper).toDto(order);
        verify(userClient).getUserById(1);
    }

    @Test
    void test_getOrdersByIds() {
        OrderItem orderItem1 = new OrderItem();
        OrderItem orderItem2 = new OrderItem();
        OrderItemGetDTO orderItemGetDTO1 = new OrderItemGetDTO(1, 1, "Iphone 14",
                new BigDecimal("1199.99"), 2);
        OrderItemGetDTO orderItemGetDTO2 = new OrderItemGetDTO(2, 1, "Iphone 14",
                new BigDecimal("1199.99"), 3);
        UserGetDTO userGetDTO = new UserGetDTO(1, "John",
                "Diamond", LocalDate.of(2000, 1, 1),
                "Mail@gmail.com", List.of(new CardGetDTO(1, 1, "1234567891018121",
                "JOHN DIAMOND", LocalDate.of(2027, 1, 1))));
        List<Order> orders = List.of(
                new Order(1, 1, OrderStatus.NEW, LocalDateTime.now(), List.of(orderItem1)),
                new Order(2, 1, OrderStatus.NEW, LocalDateTime.now(), List.of(orderItem2))
        );
        List<OrderGetDTO> orderGetDTOs = List.of(
                new OrderGetDTO(1, OrderStatus.NEW, LocalDateTime.now(), List.of(orderItemGetDTO1), userGetDTO),
                new OrderGetDTO(2, OrderStatus.NEW, LocalDateTime.now(), List.of(orderItemGetDTO2), userGetDTO)
        );
        when(orderRepository.findAllById(List.of(1, 2))).thenReturn(orders);
        when(orderGetDTOMapper.toDto(orders.get(0))).thenReturn(orderGetDTOs.get(0));
        when(orderGetDTOMapper.toDto(orders.get(1))).thenReturn(orderGetDTOs.get(1));
        when(userClient.getUserById(1)).thenReturn(userGetDTO);

        List<OrderGetDTO> result = orderService.getOrdersByIds(List.of(1, 2));

        assertEquals(2, result.size());
        assertEquals(orderGetDTOs.get(0), result.get(0));
        assertEquals(orderGetDTOs.get(1), result.get(1));
        verify(orderRepository).findAllById(List.of(1, 2));
        verify(orderGetDTOMapper).toDto(orders.get(0));
        verify(orderGetDTOMapper).toDto(orders.get(1));
        verify(userClient, times(2)).getUserById(1);
    }

    @Test
    void test_createOrder() {
        Integer userId = 1;
        OrderItemCreateDTO itemDTO = new OrderItemCreateDTO(1, 2);
        OrderCreateDTO createDTO = new OrderCreateDTO(List.of(itemDTO));

        Order order = new Order();
        order.setUserId(userId);

        Item item = new Item(1, "iPhone", new BigDecimal("1199.99"), null);
        OrderItem orderItem = OrderItem.builder().order(order).item(item).quantity(2).build();
        order.addOrderItem(orderItem);

        UserGetDTO userDTO = new UserGetDTO(userId, "John", "Doe", LocalDate.of(1990, 1, 1),
                "john@example.com", List.of());

        OrderGetDTO orderGetDTO = new OrderGetDTO(1, OrderStatus.NEW, LocalDateTime.now(), List.of(), userDTO);
        String authentication = "Bearer fake.jwt.token";
        String token = authentication.substring(7);
        Claims claims = mock(Claims.class);
        OrderKafkaDTO kafkaDTO = new OrderKafkaDTO(1,1,new BigDecimal("1199.99"));
        when(claims.getSubject()).thenReturn(String.valueOf(userId));

        when(jwtUtils.getAccessClaims(token)).thenReturn(claims);
        when(orderCreateDTOMapper.toEntity(createDTO)).thenReturn(order);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(orderGetDTOMapper.toDto(order)).thenReturn(orderGetDTO);
        when(userClient.getUserById(userId)).thenReturn(userDTO);


        OrderGetDTO result = orderService.create(createDTO, authentication);

        assertNotNull(result);
        verify(orderRepository).save(order);
        verify(userClient).getUserById(userId);
        verify(orderCreateDTOMapper).toEntity(createDTO);
        verify(orderGetDTOMapper).toDto(order);
    }
    @Test
    void test_getAllOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = new Order(1, 1, OrderStatus.NEW, LocalDateTime.now(), List.of());
        Page<Order> page = new PageImpl<>(List.of(order));
        UserGetDTO user = new UserGetDTO(1, "Name", "Surname", LocalDate.of(1990,1,1), "email", List.of());
        OrderGetDTO dto = new OrderGetDTO(1, OrderStatus.NEW, LocalDateTime.now(), List.of(), user);


        when(orderRepository.findAll(pageable)).thenReturn(page);
        when(orderGetDTOMapper.toDto(order)).thenReturn(dto);
        when(userClient.getUserById(1)).thenReturn(user);

        Page<OrderGetDTO> result = orderService.getAllOrders(pageable);

        assertEquals(1, result.getContent().size());
        verify(orderRepository).findAll(pageable);
        verify(userClient).getUserById(1);
        verify(orderGetDTOMapper).toDto(order);
    }
    @Test
    void test_getOrderByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = new Order(1, 1, OrderStatus.NEW, LocalDateTime.now(), List.of());
        Page<Order> page = new PageImpl<>(List.of(order));
        UserGetDTO user = new UserGetDTO(1, "Name", "Surname",
                LocalDate.of(1990,1,1), "email", List.of());
        OrderGetDTO dto = new OrderGetDTO(1, OrderStatus.NEW, LocalDateTime.now(), List.of(), user);


        when(orderRepository.findByStatus(OrderStatus.NEW, pageable)).thenReturn(page);
        when(orderGetDTOMapper.toDto(order)).thenReturn(dto);
        when(userClient.getUserById(1)).thenReturn(user);

        Page<OrderGetDTO> result = orderService.getOrderByStatus(pageable, "NEW");

        assertEquals(1, result.getContent().size());
        verify(orderRepository).findByStatus(OrderStatus.NEW, pageable);
        verify(userClient).getUserById(1);
        verify(orderGetDTOMapper).toDto(order);
    }
    @Test
    void test_update() {
        Integer id = 1;
        Order order = new Order(id, 1, OrderStatus.NEW, LocalDateTime.now(), List.of());
        OrderUpdateDTO updateDTO = new OrderUpdateDTO( List.of());
        UserGetDTO user = new UserGetDTO(1, "Name", "Surname",
                LocalDate.of(1990,1,1), "email", List.of());
        OrderGetDTO dto = new OrderGetDTO(id, OrderStatus.SUCCESS, LocalDateTime.now(), List.of(), user);


        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        doNothing().when(orderUpdateDTOMapper).updateOrder(order, updateDTO, itemRepository);
        when(orderGetDTOMapper.toDto(order)).thenReturn(dto);
        when(userClient.getUserById(1)).thenReturn(user);

        OrderGetDTO result = orderService.update(updateDTO, id);

        assertEquals(id, result.id());
        verify(orderRepository).findById(id);
        verify(orderUpdateDTOMapper).updateOrder(order, updateDTO, itemRepository);
        verify(orderGetDTOMapper).toDto(order);
        verify(userClient).getUserById(1);

    }
    @Test
    void test_delete() {
        when(orderRepository.existsById(1)).thenReturn(true);
        orderService.delete(1);
        verify(orderRepository).deleteById(1);
    }



}
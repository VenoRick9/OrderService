package by.baraznov.orderservice.service.impl;

import by.baraznov.orderservice.client.UserClient;
import by.baraznov.orderservice.dto.UserGetDTO;
import by.baraznov.orderservice.dto.order.OrderCreateDTO;
import by.baraznov.orderservice.dto.order.OrderGetDTO;
import by.baraznov.orderservice.dto.order.OrderUpdateDTO;
import by.baraznov.orderservice.mapper.order.OrderCreateDTOMapper;
import by.baraznov.orderservice.mapper.order.OrderGetDTOMapper;
import by.baraznov.orderservice.mapper.order.OrderUpdateDTOMapper;
import by.baraznov.orderservice.model.Order;
import by.baraznov.orderservice.model.OrderItem;
import by.baraznov.orderservice.model.OrderStatus;
import by.baraznov.orderservice.repository.ItemRepository;
import by.baraznov.orderservice.repository.OrderRepository;
import by.baraznov.orderservice.service.OrderService;
import by.baraznov.orderservice.util.ItemNotFound;
import by.baraznov.orderservice.util.JwtUtils;
import by.baraznov.orderservice.util.OrderNotFound;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderCreateDTOMapper orderCreateDTOMapper;
    private final OrderUpdateDTOMapper orderUpdateDTOMapper;
    private final OrderGetDTOMapper orderGetDTOMapper;
    private final UserClient userClient;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public OrderGetDTO create(OrderCreateDTO orderCreateDTO, String authentication) {
        String token = authentication.startsWith("Bearer ") ?
                authentication.substring(7) : authentication;
        Claims claims = jwtUtils.getAccessClaims(token);
        Integer userId = Integer.valueOf(claims.getSubject());
        Order order = orderCreateDTOMapper.toEntity(orderCreateDTO);
        order.setUserId(userId);
        for (int i = 0; i < orderCreateDTO.orderItems().size(); i++) {
            int itemId = i;
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .quantity(orderCreateDTO.orderItems().get(itemId).quantity())
                    .item(itemRepository.findById(orderCreateDTO.orderItems().get(itemId).itemId())
                            .orElseThrow(() -> new ItemNotFound("Item with id " +
                                    orderCreateDTO.orderItems().get(itemId).itemId() + " not found")))
                    .build();
            order.addOrderItem(orderItem);
        }
        orderRepository.save(order);
        return mergeOrderWithUser(orderGetDTOMapper.toDto(order), userClient.getUserById(userId));
    }

    @Override
    public OrderGetDTO getOrderById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFound("Order with id " + id + " doesn't exist"));
        return mergeOrderWithUser(orderGetDTOMapper.toDto(order), userClient.getUserById(order.getUserId()));
    }

    @Override
    public Page<OrderGetDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(s -> mergeOrderWithUser(orderGetDTOMapper.toDto(s),
                userClient.getUserById(s.getUserId())));
    }

    @Override
    public List<OrderGetDTO> getOrdersByIds(List<Integer> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("List with ids cannot be null");
        }
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return orderRepository.findAllById(ids).stream().
                map(s ->
                        mergeOrderWithUser(orderGetDTOMapper.toDto(s),
                                userClient.getUserById(s.getUserId()))).
                collect(Collectors.toList());
    }

    @Override
    public Page<OrderGetDTO> getOrderByStatus(Pageable pageable, String status) {
        return orderRepository.findByStatus(OrderStatus.valueOf(status), pageable).
                map(s -> mergeOrderWithUser(orderGetDTOMapper.toDto(s),
                        userClient.getUserById(s.getUserId())));
    }

    @Override
    @Transactional
    public OrderGetDTO update(OrderUpdateDTO orderUpdateDTO, Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFound("Order with id " + id + " doesn't exist"));

        orderUpdateDTOMapper.updateOrder(order, orderUpdateDTO, itemRepository);
        orderRepository.save(order);
        return mergeOrderWithUser(orderGetDTOMapper.toDto(order),
                userClient.getUserById(order.getUserId()));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFound("Order with id " + id + " doesn't exist");
        }
        orderRepository.deleteById(id);

    }

    private OrderGetDTO mergeOrderWithUser(OrderGetDTO orderGetDTO, UserGetDTO userGetDTO) {
        return new OrderGetDTO(orderGetDTO.id(),
                orderGetDTO.status(),
                orderGetDTO.creationDate(),
                orderGetDTO.orderItems(),
                userGetDTO);

    }
}

package by.baraznov.orderservice.service;

import by.baraznov.orderservice.dto.order.OrderCreateDTO;
import by.baraznov.orderservice.dto.order.OrderGetDTO;
import by.baraznov.orderservice.dto.order.OrderUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderGetDTO create(OrderCreateDTO orderCreateDTO);

    OrderGetDTO getOrderById(Integer id);

    Page<OrderGetDTO> getAllOrders(Pageable pageable);
    List<OrderGetDTO> getOrdersByIds(List<Integer> ids);

    Page<OrderGetDTO> getOrderByStatus(Pageable pageable, String status);

    OrderGetDTO update(OrderUpdateDTO orderUpdateDTO, Integer id);

    void delete(Integer id);
}

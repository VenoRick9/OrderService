package by.baraznov.orderservice.controller;

import by.baraznov.orderservice.dto.PageResponse;
import by.baraznov.orderservice.dto.order.OrderCreateDTO;
import by.baraznov.orderservice.dto.order.OrderGetDTO;
import by.baraznov.orderservice.dto.order.OrderUpdateDTO;
import by.baraznov.orderservice.model.OrderStatus;
import by.baraznov.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PageResponse<OrderGetDTO>> getAllOrders(
            @PageableDefault(page = 0, size = 10, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.toPageResponse(orderService.getAllOrders(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderGetDTO> getOrderById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderGetDTO>> getAllOrdersByIds(@RequestParam List<Integer> ids) {
        return ResponseEntity.ok(orderService.getOrdersByIds(ids));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderGetDTO>> getAllOrdersByStatus(@RequestParam OrderStatus status,
                @PageableDefault(page = 0, size = 10, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.toPageResponse(orderService.
                getOrderByStatus(pageable, status.toString())));
    }
    @PostMapping
    public ResponseEntity<OrderGetDTO> create(@RequestBody OrderCreateDTO orderCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(orderCreateDTO));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<OrderGetDTO> update(@RequestBody OrderUpdateDTO orderUpdateDTO, @PathVariable Integer id) {
        return ResponseEntity.ok(orderService.update(orderUpdateDTO, id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderGetDTO> delete(@PathVariable Integer id) {
        orderService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package by.baraznov.orderservice.repository;

import by.baraznov.orderservice.model.Order;
import by.baraznov.orderservice.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(Integer userId);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}

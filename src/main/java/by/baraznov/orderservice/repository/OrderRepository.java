package by.baraznov.orderservice.repository;

import by.baraznov.orderservice.model.Order;
import by.baraznov.orderservice.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value="select o from Order o left join fetch o.orderItems oi " +
            "left join fetch oi.item where o.status = :status",
            countQuery = "SELECT COUNT(o) FROM Order o")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    @Override
    @Query("select o from Order o left join fetch o.orderItems oi left join fetch oi.item where o.id = :id")
    Optional<Order> findById(@Param("id") Integer id);

    @Override
    @Query(value = "select o from Order o left join fetch o.orderItems oi left join fetch oi.item",
            countQuery = "SELECT COUNT(o) FROM Order o")
    Page<Order> findAll(Pageable pageable);

    @Override
    @Query("select o from Order o left join fetch o.orderItems oi left join fetch oi.item where o.id in :ids")
    List<Order> findAllById(@Param("ids") Iterable<Integer> ids);
}

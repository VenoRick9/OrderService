package by.baraznov.orderservice.mapper.order;

import by.baraznov.orderservice.dto.order.OrderUpdateDTO;
import by.baraznov.orderservice.dto.orderitem.OrderItemUpdateDTO;
import by.baraznov.orderservice.mapper.BaseMapper;
import by.baraznov.orderservice.mapper.orderitem.OrderItemUpdateDTOMapper;
import by.baraznov.orderservice.model.Order;
import by.baraznov.orderservice.model.OrderItem;
import by.baraznov.orderservice.repository.ItemRepository;
import by.baraznov.orderservice.util.ItemNotFound;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(config = BaseMapper.class, uses = OrderItemUpdateDTOMapper.class)
public interface OrderUpdateDTOMapper extends BaseMapper<Order, OrderUpdateDTO> {
    @Mapping(target = "orderItems", source = "orderItems")
    OrderUpdateDTO toDto(Order order);

    @Mapping(target = "orderItems", ignore = true)
    Order merge(@MappingTarget Order entity, OrderUpdateDTO dto);

    default void updateOrder(@MappingTarget Order entity, OrderUpdateDTO dto,
                             @Context ItemRepository itemRepository) {
        merge(entity, dto);
        if (dto.orderItems() != null) {
            updateOrderItemsCollection(entity, dto.orderItems(), itemRepository);
        }
    }

    private void updateOrderItemsCollection(Order order, List<OrderItemUpdateDTO> itemDTOs,
                                            ItemRepository itemRepository) {
        Map<Integer, OrderItem> existingItems = order.getOrderItems().stream()
                .collect(Collectors.toMap(OrderItem::getId, Function.identity()));

        order.getOrderItems().clear();
        for (OrderItemUpdateDTO dto : itemDTOs) {
            OrderItem orderItem = existingItems.getOrDefault(dto.id(), new OrderItem());
            updateOrderItem(orderItem, dto, order, itemRepository);
            order.getOrderItems().add(orderItem);
        }
    }

    private void updateOrderItem(OrderItem orderItem, OrderItemUpdateDTO dto,
                                 Order order, ItemRepository itemRepository) {
        orderItem.setQuantity(dto.quantity());
        orderItem.setOrder(order);
        orderItem.setItem(itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new ItemNotFound("Item not found")));
    }
}

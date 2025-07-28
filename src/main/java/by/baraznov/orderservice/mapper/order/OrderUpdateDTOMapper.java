package by.baraznov.orderservice.mapper.order;

import by.baraznov.orderservice.dto.order.OrderUpdateDTO;
import by.baraznov.orderservice.mapper.BaseMapper;
import by.baraznov.orderservice.mapper.orderitem.OrderItemUpdateDTOMapper;
import by.baraznov.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, uses = OrderItemUpdateDTOMapper.class)
public interface OrderUpdateDTOMapper extends BaseMapper<Order, OrderUpdateDTO> {
    @Mapping(target = "orderItems", source = "orderItems")
    OrderUpdateDTO toDto(Order order);
}

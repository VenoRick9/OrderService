package by.baraznov.orderservice.mapper.order;

import by.baraznov.orderservice.dto.order.OrderGetDTO;
import by.baraznov.orderservice.mapper.BaseMapper;
import by.baraznov.orderservice.mapper.orderitem.OrderItemGetDTOMapper;
import by.baraznov.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, uses = OrderItemGetDTOMapper.class)
public interface OrderGetDTOMapper extends BaseMapper<Order, OrderGetDTO> {
    @Mapping(target = "orderItems", source = "orderItems")
    OrderGetDTO toDto(Order order);
}

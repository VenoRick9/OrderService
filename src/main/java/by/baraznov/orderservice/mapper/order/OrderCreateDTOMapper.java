package by.baraznov.orderservice.mapper.order;

import by.baraznov.orderservice.dto.order.OrderCreateDTO;
import by.baraznov.orderservice.mapper.BaseMapper;
import by.baraznov.orderservice.mapper.orderitem.OrderItemCreateDTOMapper;
import by.baraznov.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, uses = OrderItemCreateDTOMapper.class)
public interface OrderCreateDTOMapper extends BaseMapper<Order, OrderCreateDTO> {
    @Mapping(target = "orderItems", source = "orderItems")
    OrderCreateDTO toDto(Order order);
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderCreateDTO orderCreateDTO);
}

package by.baraznov.orderservice.mapper.orderitem;

import by.baraznov.orderservice.dto.orderitem.OrderItemCreateDTO;
import by.baraznov.orderservice.mapper.BaseMapper;
import by.baraznov.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class)
public interface OrderItemCreateDTOMapper extends BaseMapper<OrderItem, OrderItemCreateDTO> {
    @Mapping(source = "item.id", target = "itemId")
    OrderItemCreateDTO toDto(OrderItem orderItem);
}

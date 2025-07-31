package by.baraznov.orderservice.mapper.orderitem;

import by.baraznov.orderservice.dto.orderitem.OrderItemUpdateDTO;
import by.baraznov.orderservice.mapper.BaseMapper;
import by.baraznov.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class)
public interface OrderItemUpdateDTOMapper extends BaseMapper<OrderItem, OrderItemUpdateDTO> {
    @Mapping(source = "item.id", target = "itemId")
    OrderItemUpdateDTO toDto(OrderItem orderItem);
}

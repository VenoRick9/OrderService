package by.baraznov.orderservice.mapper.orderitem;

import by.baraznov.orderservice.dto.orderitem.OrderItemGetDTO;
import by.baraznov.orderservice.mapper.BaseMapper;
import by.baraznov.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class)
public interface OrderItemGetDTOMapper extends BaseMapper<OrderItem, OrderItemGetDTO> {
    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "item.name", target = "itemName")
    @Mapping(source = "item.price", target = "itemPrice")
    OrderItemGetDTO toDto(OrderItem orderItem);
}

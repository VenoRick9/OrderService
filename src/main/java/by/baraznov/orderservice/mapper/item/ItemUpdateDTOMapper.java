package by.baraznov.orderservice.mapper.item;

import by.baraznov.orderservice.dto.item.ItemUpdateDTO;
import by.baraznov.orderservice.mapper.BaseMapper;
import by.baraznov.orderservice.model.Item;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface ItemUpdateDTOMapper extends BaseMapper<Item, ItemUpdateDTO> {
}

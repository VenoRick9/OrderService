package by.baraznov.orderservice.service;

import by.baraznov.orderservice.dto.item.ItemCreateDTO;
import by.baraznov.orderservice.dto.item.ItemGetDTO;
import by.baraznov.orderservice.dto.item.ItemUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    ItemGetDTO create(ItemCreateDTO itemCreateDTO);

    ItemGetDTO getItemById(Integer id);

    Page<ItemGetDTO> getAllItems(Pageable pageable);

    ItemGetDTO getItemByName(String name);

    ItemGetDTO update(ItemUpdateDTO itemUpdateDTO, Integer id);

    void delete(Integer id);
}

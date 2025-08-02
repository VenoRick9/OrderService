package by.baraznov.orderservice.service.impl;

import by.baraznov.orderservice.dto.item.ItemCreateDTO;
import by.baraznov.orderservice.dto.item.ItemGetDTO;
import by.baraznov.orderservice.dto.item.ItemUpdateDTO;
import by.baraznov.orderservice.mapper.item.ItemCreateDTOMapper;
import by.baraznov.orderservice.mapper.item.ItemGetDTOMapper;
import by.baraznov.orderservice.mapper.item.ItemUpdateDTOMapper;
import by.baraznov.orderservice.model.Item;
import by.baraznov.orderservice.repository.ItemRepository;
import by.baraznov.orderservice.service.ItemService;
import by.baraznov.orderservice.util.ItemAlreadyExist;
import by.baraznov.orderservice.util.ItemNotFound;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemGetDTOMapper itemGetDTOMapper;
    private final ItemUpdateDTOMapper itemUpdateDTOMapper;
    private final ItemCreateDTOMapper itemCreateDTOMapper;
    @Override
    @Transactional
    public ItemGetDTO create(ItemCreateDTO itemCreateDTO) {
        Item item = itemCreateDTOMapper.toEntity(itemCreateDTO);
        if(itemRepository.existsByName((item.getName()))) {
            throw new ItemAlreadyExist("Item name " + item.getName() + " already exist");
        }
        return itemGetDTOMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemGetDTO getItemById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return itemGetDTOMapper.toDto(itemRepository.findById(id)
                .orElseThrow(()-> new ItemNotFound("Item with id " + id + " doesn't exist")));
    }

    @Override
    public Page<ItemGetDTO> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).map(itemGetDTOMapper::toDto);
    }

    @Override
    public ItemGetDTO getItemByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return itemGetDTOMapper.toDto(itemRepository.findByName(name)
                .orElseThrow(()-> new ItemNotFound("Item with name " + name + " doesn't exist")));
    }

    @Override
    @Transactional
    public ItemGetDTO update(ItemUpdateDTO itemUpdateDTO, Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        Item item = itemRepository.findById(id)
                .orElseThrow(()-> new ItemNotFound("Item with id " + id + " doesn't exist"));
        if(!itemUpdateDTO.name().equals(item.getName()) && itemRepository.existsByName(itemUpdateDTO.name()))  {
            throw new ItemAlreadyExist("Item name " + item.getName() + " already exist");
        }
        itemUpdateDTOMapper.merge(item, itemUpdateDTO);
        itemRepository.save(item);
        return itemGetDTOMapper.toDto(item);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFound("Item with id " + id + " doesn't exist");
        }
        itemRepository.deleteById(id);
    }
}

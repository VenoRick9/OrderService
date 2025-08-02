package by.baraznov.orderservice.service.impl;

import by.baraznov.orderservice.dto.item.ItemCreateDTO;
import by.baraznov.orderservice.dto.item.ItemGetDTO;
import by.baraznov.orderservice.dto.item.ItemUpdateDTO;
import by.baraznov.orderservice.mapper.item.ItemCreateDTOMapper;
import by.baraznov.orderservice.mapper.item.ItemGetDTOMapper;
import by.baraznov.orderservice.mapper.item.ItemUpdateDTOMapper;
import by.baraznov.orderservice.model.Item;
import by.baraznov.orderservice.model.OrderItem;
import by.baraznov.orderservice.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemGetDTOMapper itemGetDTOMapper;
    @Mock
    private ItemCreateDTOMapper itemCreateDTOMapper;
    @Mock
    private ItemUpdateDTOMapper itemUpdateDTOMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void test_getAllItems() {
        Pageable pageable = PageRequest.of(0, 2);
        OrderItem orderItem = new OrderItem();

        Item item1 = new Item(1, "Iphone 14", new BigDecimal("1199.99"), List.of(orderItem));
        Item item2 = new Item(2, "Iphone 15", new BigDecimal("1299.99"), List.of(orderItem));
        ItemGetDTO dto1 = new ItemGetDTO(1, "Iphone 14", new BigDecimal("1199.99"));
        ItemGetDTO dto2 = new ItemGetDTO(2, "Iphone 15", new BigDecimal("1299.99"));

        Page<Item> itemPage = new PageImpl<>(List.of(item1, item2), pageable, 2);
        when(itemRepository.findAll(pageable)).thenReturn(itemPage);
        when(itemGetDTOMapper.toDto(item1)).thenReturn(dto1);
        when(itemGetDTOMapper.toDto(item2)).thenReturn(dto2);

        Page<ItemGetDTO> result = itemService.getAllItems(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(itemRepository).findAll(pageable);
        verify(itemGetDTOMapper).toDto(item1);
        verify(itemGetDTOMapper).toDto(item2);
    }
    @Test
    void test_createItem() {
        ItemCreateDTO createDTO = new ItemCreateDTO("iPad", new BigDecimal("899.99"));
        Item item = new Item(null, "iPad", new BigDecimal("899.99"), List.of());
        Item savedItem = new Item(1, "iPad", new BigDecimal("899.99"), List.of());
        ItemGetDTO dto = new ItemGetDTO(1, "iPad", new BigDecimal("899.99"));

        when(itemCreateDTOMapper.toEntity(createDTO)).thenReturn(item);
        when(itemRepository.existsByName("iPad")).thenReturn(false);
        when(itemRepository.save(item)).thenReturn(savedItem);
        when(itemGetDTOMapper.toDto(savedItem)).thenReturn(dto);

        ItemGetDTO result = itemService.create(createDTO);

        assertEquals(dto, result);
        verify(itemRepository).existsByName("iPad");
        verify(itemRepository).save(item);
    }

    @Test
    void test_getItemById() {
        Item item = new Item(1, "iPhone", new BigDecimal("999.99"), List.of());
        ItemGetDTO dto = new ItemGetDTO(1, "iPhone", new BigDecimal("999.99"));

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemGetDTOMapper.toDto(item)).thenReturn(dto);

        ItemGetDTO result = itemService.getItemById(1);

        assertEquals(dto, result);
        verify(itemRepository).findById(1);
        verify(itemGetDTOMapper).toDto(item);
    }
    @Test
    void test_getItemByName() {
        Item item = new Item(1, "MacBook", new BigDecimal("1599.99"), List.of());
        ItemGetDTO dto = new ItemGetDTO(1, "MacBook", new BigDecimal("1599.99"));

        when(itemRepository.findByName("MacBook")).thenReturn(Optional.of(item));
        when(itemGetDTOMapper.toDto(item)).thenReturn(dto);

        ItemGetDTO result = itemService.getItemByName("MacBook");

        assertEquals(dto, result);
        verify(itemRepository).findByName("MacBook");
        verify(itemGetDTOMapper).toDto(item);
    }
    @Test
    void test_updateItem() {
        Integer id = 1;
        ItemUpdateDTO updateDTO = new ItemUpdateDTO("Galaxy S22", new BigDecimal("1099.99"));
        Item existing = new Item(1, "Galaxy S21", new BigDecimal("999.99"), List.of());
        ItemGetDTO dto = new ItemGetDTO(1, "Galaxy S22", new BigDecimal("1099.99"));

        when(itemRepository.findById(id)).thenReturn(Optional.of(existing));
        when(itemRepository.existsByName(updateDTO.name())).thenReturn(false);
        doAnswer(inv -> {
            existing.setName(updateDTO.name());
            existing.setPrice(updateDTO.price());
            return null;
        }).when(itemUpdateDTOMapper).merge(existing, updateDTO);

        when(itemRepository.save(existing)).thenReturn(existing);
        when(itemGetDTOMapper.toDto(existing)).thenReturn(dto);

        ItemGetDTO result = itemService.update(updateDTO, id);

        assertEquals(dto, result);
        verify(itemRepository).findById(id);
        verify(itemRepository).save(existing);
        verify(itemGetDTOMapper).toDto(existing);
    }
    @Test
    void test_deleteItem_success() {
        when(itemRepository.existsById(1)).thenReturn(true);

        itemService.delete(1);

        verify(itemRepository).deleteById(1);
    }




}
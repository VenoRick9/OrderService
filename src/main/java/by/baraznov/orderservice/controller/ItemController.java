package by.baraznov.orderservice.controller;

import by.baraznov.orderservice.dto.PageResponse;
import by.baraznov.orderservice.dto.item.ItemCreateDTO;
import by.baraznov.orderservice.dto.item.ItemGetDTO;
import by.baraznov.orderservice.dto.item.ItemUpdateDTO;
import by.baraznov.orderservice.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    @GetMapping
    public ResponseEntity<PageResponse<ItemGetDTO>> getAllItems(
            @PageableDefault(page = 0, size = 10, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.toPageResponse(itemService.getAllItems(pageable)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ItemGetDTO> getItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }
    @GetMapping
    public ResponseEntity<ItemGetDTO> getItemByName(@RequestParam String name) {
        return ResponseEntity.ok(itemService.getItemByName(name));
    }
    @PostMapping
    public ResponseEntity<ItemGetDTO> create(@RequestBody ItemCreateDTO itemCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(itemCreateDTO));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ItemGetDTO> update(@RequestBody ItemUpdateDTO itemUpdateDTO, @PathVariable Integer id) {
        return ResponseEntity.ok(itemService.update(itemUpdateDTO, id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        itemService.delete(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}

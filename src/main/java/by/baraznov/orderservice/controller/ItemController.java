package by.baraznov.orderservice.controller;

import by.baraznov.orderservice.dto.PageResponse;
import by.baraznov.orderservice.dto.item.ItemCreateDTO;
import by.baraznov.orderservice.dto.item.ItemGetDTO;
import by.baraznov.orderservice.dto.item.ItemUpdateDTO;
import by.baraznov.orderservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    @GetMapping
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<PageResponse<ItemGetDTO>> getAllItems(
            @PageableDefault(page = 0, size = 10, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.toPageResponse(itemService.getAllItems(pageable)));
    }
    @GetMapping("/{id}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<ItemGetDTO> getItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }
    @GetMapping(params = "name")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<ItemGetDTO> getItemByName(String name) {
        return ResponseEntity.ok(itemService.getItemByName(name));
    }
    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ItemGetDTO> create(@RequestBody @Valid ItemCreateDTO itemCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(itemCreateDTO));
    }
    @PatchMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ItemGetDTO> update(@RequestBody @Valid ItemUpdateDTO itemUpdateDTO, @PathVariable Integer id) {
        return ResponseEntity.ok(itemService.update(itemUpdateDTO, id));
    }
    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        itemService.delete(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}

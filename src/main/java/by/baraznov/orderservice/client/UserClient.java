package by.baraznov.orderservice.client;

import by.baraznov.orderservice.dto.UserGetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${external.server.baseUrl}")
public interface UserClient {

    @GetMapping("/users/{id}")
    UserGetDTO getUserById(@PathVariable UUID id);
}
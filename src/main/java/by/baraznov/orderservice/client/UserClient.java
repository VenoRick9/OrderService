package by.baraznov.orderservice.client;

import by.baraznov.orderservice.config.FeignClientConfig;
import by.baraznov.orderservice.dto.UserGetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8080", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    UserGetDTO getUserById(@PathVariable Integer id);
}
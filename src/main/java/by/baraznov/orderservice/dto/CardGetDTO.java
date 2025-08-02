package by.baraznov.orderservice.dto;

import java.time.LocalDate;

public record CardGetDTO(
        Integer id,
        Integer userId,
        String number,
        String holder,
        LocalDate expirationDate
) {
}

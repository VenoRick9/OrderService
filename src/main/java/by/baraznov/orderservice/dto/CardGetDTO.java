package by.baraznov.orderservice.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CardGetDTO(
        Integer id,
        UUID userId,
        String number,
        String holder,
        LocalDate expirationDate
) {
}

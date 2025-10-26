package by.baraznov.orderservice.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserGetDTO(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        List<CardGetDTO> cards
) {
}

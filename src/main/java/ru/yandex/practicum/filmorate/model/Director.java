package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Director {
    @NotNull(message = "ID режиссёра не может быть пустым")  // ← Проверяет ID
    @Positive(message = "ID режиссёра должен быть положительным")
    private Long id;

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}
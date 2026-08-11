package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Director {
    @Positive(message = "ID режиссёра должен быть положительным")
    private Long id;

    private String name;
}
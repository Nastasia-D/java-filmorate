package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Review {

    private Long reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва (positive/negative) обязателен")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя обязателен")
    @Positive(message = "ID пользователя должен быть положительным")
    private Long userId;

    @NotNull(message = "ID фильма обязателен")
    @Positive(message = "ID фильма должен быть положительным")
    private Long filmId;

    private Integer useful = 0;
}
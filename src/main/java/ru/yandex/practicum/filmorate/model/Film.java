package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @ValidReleaseDate(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность обязательна")
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    private final Set<Long> likes = new HashSet<>();

    @Valid
    private Set<Genre> genres = new HashSet<>();

    @NotNull(message = "MPA рейтинг обязателен")
    @Valid
    private Mpa mpa;

    @Valid
    private Set<Director> directors = new HashSet<>();
}
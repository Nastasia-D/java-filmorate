package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос на создание фильма {}", film.getName());

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка валидации: указано пустое название фильма {}", film.getName());
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: превышен лимит символов в описании {}", film.getDescription());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации: указана неверная дата релиза {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Ошибка валидации: указана продолжительность фильма меньше 0 - {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            log.warn("Ошибка: не указан ID фильма");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Ошибка: фильм с ID {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.warn("Ошибка валидации: указано пустое название фильма {}", newFilm.getName());
            throw new ValidationException("Название не может быть пустым");
        }

        if (newFilm.getDescription() == null || newFilm.getDescription().length() > 200) {
            log.warn("Ошибка валидации: превышен лимит символов в описании {}", newFilm.getDescription());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (newFilm.getReleaseDate() == null || newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации: указана неверная дата релиза {}", newFilm.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (newFilm.getDuration() == null || newFilm.getDuration() <= 0) {
            log.warn("Ошибка валидации: указана продолжительность фильма меньше 0 - {}", newFilm.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с ID {} успешно обновлен", newFilm.getId());
        return newFilm;

    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}

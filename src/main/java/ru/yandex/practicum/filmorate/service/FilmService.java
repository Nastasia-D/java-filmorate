package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public void addLikeFilm(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);

        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }

        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        film.getLikes().add(userId);
    }

    public Film getFilm(Long id) {
        return filmStorage.findAll().stream()
                .filter(film -> film.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк от пользователя не найден");
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}

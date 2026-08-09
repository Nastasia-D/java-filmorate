package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;// новое поле

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateMpaAndGenres(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        getFilm(film.getId());
        validateMpaAndGenres(film);
        return filmStorage.update(film);
    }

    public void addLikeFilm(Long filmId, Long userId) {
        getFilm(filmId);
        userService.getUser(userId);
        filmStorage.addLikeFilm(filmId, userId);
    }

    public Film getFilm(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public void removeLike(Long filmId, Long userId) {
        getFilm(filmId);
        userService.getUser(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

    public Optional<Film> findById(Long id) {
        return filmStorage.findById(id);
    }

    private void validateMpaAndGenres(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaStorage.findById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA с id " + film.getMpa().getId() + " не найден"));
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreStorage.findById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id " + genre.getId() + " не найден")); // ошибка CRTL-V(С)  было вот так "film.getMpa().getId()"
            }
        }
        // Валидация режиссеров
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                directorStorage.findById(director.getId())
                        .orElseThrow(() -> new NotFoundException("Режиссёр с id " + director.getId() + " не найден"));
            }
        }
    }

    // НОВЫЙ МЕТОД
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        directorStorage.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id " + directorId + " не найден"));

        if (!"year".equalsIgnoreCase(sortBy) && !"likes".equalsIgnoreCase(sortBy)) {
            throw new IllegalArgumentException("Неверный параметр сортировки. Допустимые значения: year, likes");
        }

        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> searchFilms(String query, String by) {
        List<String> searchBy = new ArrayList<>();
        if (by == null || by.isBlank()) {

            searchBy.add("title");
        } else {
            String[] parts = by.split(",");
            for (String part : parts) {
                String trimmed = part.trim().toLowerCase();
                if (trimmed.equals("title") || trimmed.equals("director")) {
                    searchBy.add(trimmed);
                }
            }
            if (searchBy.isEmpty()) {
                // Если указаны невалидные значения, ищем по названию по умолчанию
                searchBy.add("title");
            }
        }

        if (query == null || query.isBlank()) {
            throw new ValidationException("Текст для поиска не может быть пустым");
        }

        return filmStorage.searchFilms(query, searchBy);
    }
}

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

    public void delete(Long filmId) {
        getFilm(filmId);
        filmStorage.delete(filmId);
    }


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

    public List<Film> getTopFilms(Integer count, Long genreId, Integer year) {
        return filmStorage.getTopFilms(count, genreId, year);
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

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userService.getUser(userId);
        userService.getUser(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> searchFilms(String query, String by) {
        if (query == null || query.isBlank()) {
            throw new ValidationException("Текст для поиска не может быть пустым");
        }

        List<String> searchBy = new ArrayList<>();

        if (by == null || by.isBlank()) {
            searchBy.add("title");
        } else {
            String[] parts = by.split(",");
            for (String part : parts) {
                String trimmed = part.trim().toLowerCase();

                if (trimmed.isEmpty()) {
                    throw new ValidationException("Параметр 'by' не может содержать пустые значения");
                }

                if (trimmed.equals("title")) {
                    searchBy.add("title");
                } else if (trimmed.equals("director")) {
                    searchBy.add("director");
                } else {
                    throw new ValidationException(
                            "Некорректное значение параметра 'by': '" + part + "'. " +
                                    "Допустимые значения: 'title', 'director' или 'title,director'"
                    );
                }
            }

            if (searchBy.isEmpty()) {
                throw new ValidationException("Параметр 'by' не может быть пустым");
            }
        }

        return filmStorage.searchFilms(query, searchBy);
    }

}

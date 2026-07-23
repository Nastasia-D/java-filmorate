package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

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
                        .orElseThrow(() -> new NotFoundException("Жанр с id " + film.getMpa().getId() + " не найден"));
            }
        }
    }
}

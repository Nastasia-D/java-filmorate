package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findById(Long id);

    void addLikeFilm(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    void delete(Long filmId);

    Optional<Long> getSimilarUserId(Long userId);

    List<Film> getRecommendations(Long userId, Long similarUserId);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getTopFilms(Integer count, Long genreId, Integer year);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);// новый метод для получения фильмов по режжисерам

    List<Film> searchFilms(String query, List<String> searchBy);
}

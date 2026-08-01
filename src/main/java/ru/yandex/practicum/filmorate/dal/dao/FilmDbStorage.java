package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final DirectorRowMapper directorRowMapper;


    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.*, m.id AS \"mpa.id\", m.name AS \"mpa.name\" " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.id ";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(getGenresForFilm(film.getId())));
            film.setDirectors(new HashSet<>(getDirectorsForFilm(film.getId()))); // Добавляем загрузку режиссеров
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        film.setId(id);
        batchUpdateGenre(film);
        batchUpdateDirectors(film);// сохранение режисера
        return findById(id).orElse(film);
    }

    @Override
    public void addLikeFilm(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String sql = "SELECT f.*, m.id AS \"mpa.id\", m.name AS \"mpa.name\" " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "GROUP BY f.id, m.id, m.name " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(getGenresForFilm(film.getId())));
            film.setMpa(getMpaForFilm(film.getId()));
            film.setDirectors(new HashSet<>(getDirectorsForFilm(film.getId())));
        }
        return films;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        batchUpdateGenre(film);
        batchUpdateDirectors(film);// обновление режиссера
        return film;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT f.*, m.id AS \"mpa.id\", m.name AS \"mpa.name\" " +
                "FROM films As f " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper, id);

        if (filmList.isEmpty()) {
            return Optional.empty();
        }

        Film film = filmList.get(0);
        film.setGenres(new LinkedHashSet<>(getGenresForFilm(id)));
        film.setDirectors(new HashSet<>(getDirectorsForFilm(id)));//опять режиссеры
        return Optional.of(film);
    }

    private List<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.id, g.name " +
                "FROM genres AS g " +
                "INNER JOIN film_genres AS fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }

    private Mpa getMpaForFilm(Long filmId) {
        String sql = "SELECT m.id, m.name " +
                "FROM mpa AS m " +
                "INNER JOIN films AS f ON m.id = f.mpa_id " +
                "WHERE f.id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Mpa(rs.getLong("id"), rs.getString("name"));
        }, filmId).stream().findFirst().orElse(null);
    }

    private void batchUpdateGenre(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
        String sqlGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

                jdbcTemplate.batchUpdate(sqlGenres, uniqueGenres, uniqueGenres.size(), (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                });
            }

    // Новый метод для получения режиссёров фильма
    private List<Director> getDirectorsForFilm(Long filmId) {
        String sql = "SELECT d.id, d.name " +
                "FROM directors AS d " +
                "INNER JOIN film_directors AS fd ON d.id = fd.director_id " +
                "WHERE fd.film_id = ? " +
                "ORDER BY d.id ASC";
        return jdbcTemplate.query(sql, directorRowMapper, filmId);
    }

    // Новый метод для обновления режиссёров фильма
    private void batchUpdateDirectors(Film film) {
        // Удаляем старые связи
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());

        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }

        Set<Director> uniqueDirectors = new HashSet<>(film.getDirectors());
        String sqlDirectors = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sqlDirectors, uniqueDirectors, uniqueDirectors.size(), (ps, director) -> {
            ps.setLong(1, film.getId());
            ps.setLong(2, director.getId());
        });
    }

    // Новый метод для получения фильмов по режиссёру
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        String orderBy = "";
        if ("year".equalsIgnoreCase(sortBy)) {
            orderBy = "ORDER BY f.release_date";
        } else if ("likes".equalsIgnoreCase(sortBy)) {
            orderBy = "ORDER BY COUNT(l.user_id) DESC";
        } else {
            throw new IllegalArgumentException("Неверный параметр сортировки: " + sortBy);
        }

        String sql = "SELECT f.*, m.id AS \"mpa.id\", m.name AS \"mpa.name\" " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                "INNER JOIN film_directors AS fd ON f.id = fd.film_id " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id, m.id, m.name " +
                orderBy;

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, directorId);
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(getGenresForFilm(film.getId())));
            film.setDirectors(new HashSet<>(getDirectorsForFilm(film.getId())));
        }
        return films;
    }
}

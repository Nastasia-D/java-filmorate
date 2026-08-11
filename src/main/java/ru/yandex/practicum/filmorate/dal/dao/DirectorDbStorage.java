package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper;

    @Override
    public Collection<Director> findAll() {
        String sql = "SELECT * FROM directors ORDER BY id";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Optional<Director> findById(Long id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper, id);
        return directors.stream().findFirst();
    }

    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, director.getName(), director.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Режиссёр с id " + director.getId() + " не найден");
        }

        return director;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM directors WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);

        if (rowsDeleted == 0) {
            throw new NotFoundException("Режиссёр с id " + id + " не найден");
        }
    }
}
package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> findAll() {
        String sql = "SELECT * " +
                "FROM mpa " +
                "ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        });
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }, id);
        return mpaList.stream().findFirst();
    }
}

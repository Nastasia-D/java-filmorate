package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * " +
                "FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        user.setId(id);
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, is_confirmed) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, false);
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        return user;
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long friendId) {
        String sql = "SELECT u.* " +
                "FROM users AS u " +
                "INNER JOIN friends AS f1 ON u.id = f1.friend_id " +
                "INNER JOIN friends AS f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        List<User> list = jdbcTemplate.query(sql, userRowMapper, userId, friendId);
        return new HashSet<>(list);
    }

    @Override
    public Set<User> getFriends(Long userId) {
        String sql = "SELECT u.* " +
                "FROM users AS u " +
                "INNER JOIN friends AS f ON u.id = f.friend_id " +
                "WHERE f.user_id = ? AND f.is_confirmed = true";
        List<User> list = jdbcTemplate.query(sql, userRowMapper, userId);
        return new HashSet<>(list);
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.stream().findFirst();
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }
}

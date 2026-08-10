package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User getUser(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Set<User> getFriends(Long userId) {
        getUser(userId);
        return userStorage.getFriends(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        userStorage.addFriend(userId, friendId);

        feedStorage.addEvent(new Event(null, System.currentTimeMillis(), userId, EventType.FRIEND, Operation.ADD, friendId));
    }

    public void deleteFriend(Long userId, Long friendId) {
        getUser(userId);
        getUser(friendId);
        userStorage.deleteFriend(userId, friendId);
        feedStorage.addEvent(new Event(null, System.currentTimeMillis(), userId, EventType.FRIEND, Operation.REMOVE, friendId));
    }

    public Set<User> getCommonFriends(Long userId, Long friendId) {
        getUser(userId);
        getUser(friendId);

        return userStorage.getCommonFriends(userId, friendId);
    }

    public Optional<User> findById(Long id) {
        return userStorage.findById(id);
    }

    public List<Film> getRecommendations(Long userId) {
        User user = getUser(userId);
        Optional<Long> similarUserId = filmStorage.getSimilarUserId(userId);
        if (similarUserId.isEmpty()) {
            return Collections.emptyList();
        }
        return filmStorage.getRecommendations(userId, similarUserId.get());
    }

    public List<Event> getFeed(Long userId) {
        getUser(userId);
        return feedStorage.getFeed(userId);
    }
}
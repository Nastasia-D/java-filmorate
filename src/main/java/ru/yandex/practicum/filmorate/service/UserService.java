package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

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
    }

    public void deleteFriend(Long userId, Long friendId) {
        getUser(userId);
        getUser(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(Long userId, Long friendId) {
        getUser(userId);
        getUser(friendId);

        return userStorage.getCommonFriends(userId, friendId);
    }

    public Optional<User> findById(Long id) {
        return userStorage.findById(id);
    }
}

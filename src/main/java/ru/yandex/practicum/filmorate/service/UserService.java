package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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
        return userStorage.findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Set<User> getFriends(Long userId) {
        return getUser(userId).getFriends().stream()
                .map(id -> userStorage.findAll().stream()
                        .filter(user -> user.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toSet());
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Set<User> getCommonFriends(Long userId, Long friendId) {
        Set<Long> userFriends = getUser(userId).getFriends();
        Set<Long> otherFriends = getUser(friendId).getFriends();

        return userFriends.stream()
                .filter(u -> otherFriends.contains(u))
                .map(id -> userStorage.findAll().stream()
                        .filter(u -> u.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toSet());
    }

}

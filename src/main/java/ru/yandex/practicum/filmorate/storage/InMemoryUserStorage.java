package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 0;

    @Override
    public Collection<User> findAll() {

        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(++idCounter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User friend = findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public Set<User> getFriends(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.getFriends().stream()
                .map(id -> users.get(id))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long friendId) {
        User user1 = users.get(userId);
        User user2 = users.get(friendId);

        if (user1 == null || user2 == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        Set<Long> friendsIds1 = user1.getFriends();
        Set<Long> friendsIds2 = user2.getFriends();

        Set<Long> commonIds = new HashSet<>(friendsIds1);
        commonIds.retainAll(friendsIds2);

        return commonIds.stream()
                .map(id -> users.get(id))
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = users.get(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        user.getFriends().remove(friendId);
    }

}

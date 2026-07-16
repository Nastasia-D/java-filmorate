package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    Set<User> getFriends(Long userId);

    Optional<User> findById(Long id);

    void addFriend(Long userId, Long friendId);

    Set<User> getCommonFriends(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}

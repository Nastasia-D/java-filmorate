package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя с id: {}", userId);
        userService.delete(userId);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user.getLogin());
        setDefaultNameIfEmpty(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя с id: {}", user.getId());
        setDefaultNameIfEmpty(user);
        return userService.update(user);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable Long id) {
        log.info("Запрос на получение друзей пользователя с id: {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public Set<User> getCommonFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на получение общих друзей пользователей {} и {}", id, friendId);
        return userService.getCommonFriends(id, friendId);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на добавление друга {} пользователю {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на удаление друга {} у пользователя {}", friendId, id);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с id: {}", id);
        return userService.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable Long id) {
        log.info("Получен запрос на получение ленты событий пользователя с id = {}", id);
        return userService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        log.info("Запрос на получение рекомендаций для пользователя с id: {}", id);
        return userService.getRecommendations(id);
    }

    private void setDefaultNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            log.debug("Имя пользователя не указано, устанавливаем имя равным логину: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        log.debug("Пользователь с логином '{}' прошел валидацию", user.getLogin());
    }
}

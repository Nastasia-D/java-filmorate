package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {
    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        userController = new UserController();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldSaveFilmSuccessfullyWithValidFields() {
        Film film = new Film();
        film.setName("Красотка");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1990, 3, 23));
        film.setDuration(119);

        Film newFilm = filmController.create(film);
        assertNotNull(newFilm.getId(), "ID фильма не должен быть null");
        assertEquals("Красотка", newFilm.getName(), "Название фильма должно совпадать");
        assertEquals(1, filmController.findAll().size(), "В списке фильмов должен быть 1 элемент");
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsBefore1895() {
        Film film = new Film();
        film.setName("Плохая дата");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(119);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("Отрицательная продолжительность");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 12, 27));
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldSaveUserSuccessfullyWithValidFields() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("valid@email.com");
        user.setBirthday(LocalDate.of(1998, 2, 7));

        User newUser = userController.createUser(user);

        assertNotNull(newUser.getId(), "ID пользователя не должен быть null");
        assertEquals("login", newUser.getLogin(), "Логин должен совпадать");
        assertEquals(1, userController.listUsers().size(), "В списке пользователей должен быть 1 элемент");

    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("invalid-email-without-at");
        user.setBirthday(LocalDate.of(1998, 2, 7));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = new User();
        user.setLogin("log in");
        user.setEmail("valid@email.com");
        user.setBirthday(LocalDate.of(1998, 2, 7));

        assertThrows(ValidationException.class, () -> userController.createUser(user));

    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("valid-email-@without-at");
        user.setBirthday(LocalDate.of(2098, 2, 7));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentFilm() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Неверный id");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(200, 12, 27));
        film.setDuration(119);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("valid@email.com");
        user.setBirthday(LocalDate.of(1998, 2, 7));

        User newUser = userController.createUser(user);

        newUser.setLogin("newLogin");
        newUser.setEmail("new@mail.com");

        User updatedUser = userController.update(newUser);
        assertEquals("newLogin", newUser.getLogin());
        assertEquals("new@mail.com", newUser.getEmail());
        assertEquals(newUser.getId(), updatedUser.getId(), "ID не должен меняться при обновлении");
    }

    @Test
    void shouldUpdateFilmSuccessfully() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2000, 12, 27));
        film.setDuration(119);

        Film newFilm = filmController.create(film);

        newFilm.setName("Новый фильм");
        newFilm.setDescription("Новое описание");

        Film updateFilm = filmController.update(newFilm);
        assertEquals("Новый фильм", newFilm.getName());
        assertEquals("Новое описание", newFilm.getDescription());
        assertEquals(newFilm.getId(), updateFilm.getId(), "ID не должен меняться при обновлении");

    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        User user = new User();
        user.setLogin(null);
        user.setEmail("valid@email.com");
        user.setBirthday(LocalDate.of(1998, 2, 7));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginIsEmpty() {
        User user = new User();
        user.setLogin(" ");
        user.setEmail("valid@email.com");
        user.setBirthday(LocalDate.of(1998, 2, 7));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1990, 3, 23));
        film.setDuration(119);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenFilmIsNull() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1990, 3, 23));
        film.setDuration(119);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

}

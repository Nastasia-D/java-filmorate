package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FilmDbStorage.class, FilmRowMapper.class})
public class DbStorageTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM mpa");

        jdbcTemplate.update("INSERT INTO mpa (id, name) VALUES (?, ?)", 1, "0+");
        jdbcTemplate.update("INSERT INTO mpa (id, name) VALUES (?, ?)", 2, "6+");
        jdbcTemplate.update("INSERT INTO mpa (id, name) VALUES (?, ?)", 3, "12+");
        jdbcTemplate.update("INSERT INTO mpa (id, name) VALUES (?, ?)", 4, "16+");
        jdbcTemplate.update("INSERT INTO mpa (id, name) VALUES (?, ?)", 5, "18+");
    }

    @Test
    public void testFindUserFindAll() {
        User user1 = new User();
        user1.setLogin("login1");
        user1.setEmail("valid1@email.com");
        user1.setName("name1");
        user1.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser1 = userStorage.create(user1);

        User user2 = new User();
        user2.setLogin("login2");
        user2.setEmail("valid2@email.com");
        user2.setName("name2");
        user2.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser2 = userStorage.create(user2);

        Collection<User> users = userStorage.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    public void testFindFilmFindAll() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("description1");
        film1.setReleaseDate(LocalDate.of(1998, 2, 7));
        film1.setDuration(120);
        film1.setMpa(mpa);

        Film createFilm1 = filmStorage.create(film1);

        Film film2 = new Film();
        film2.setName("name2");
        film2.setDescription("description2");
        film2.setReleaseDate(LocalDate.of(1998, 2, 7));
        film2.setDuration(120);
        film2.setMpa(mpa);

        Film createFilm2 = filmStorage.create(film2);

        Collection<Film> films = filmStorage.findAll();

        assertThat(films).hasSize(2);
    }

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("valid@email.com");
        user.setName("name");
        user.setBirthday(LocalDate.of(1998, 2, 7));
        User createdUser = userStorage.create(user);
        Optional<User> userOptional = userStorage.findById(createdUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(createdUser.getId());
                    assertThat(u.getLogin()).isEqualTo("login");
                });
    }

    @Test
    public void testFindUserByIdNotFound() {
        Optional<User> userOptional = userStorage.findById(999L);

        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    public void testFindFilmById() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1998, 2, 7));
        film.setDuration(120);
        film.setMpa(mpa);
        Film createFilm = filmStorage.create(film);
        Optional<Film> filmOptional = filmStorage.findById(createFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getId()).isEqualTo(createFilm.getId());
                    assertThat(f.getName()).isEqualTo("name");
                });
    }

    @Test
    public void testFindFilmByIdNotFound() {

        Optional<Film> filmOptional = filmStorage.findById(999L);

        assertThat(filmOptional)
                .isEmpty();
    }

    @Test
    public void testFindUserCreate() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("valid@email.com");
        user.setName("name");
        user.setBirthday(LocalDate.of(1998, 2, 7));
        User createUser = userStorage.create(user);

        assertThat(createUser).isNotNull();
        assertThat(createUser.getId()).isNotNull();
        assertThat(createUser.getLogin()).isEqualTo("login");

    }

    @Test
    public void testFindFilmCreate() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1998, 2, 7));
        film.setDuration(120);
        film.setMpa(mpa);
        Film createFilm = filmStorage.create(film);

        assertThat(createFilm).isNotNull();
        assertThat(createFilm.getId()).isNotNull();
        assertThat(createFilm.getName().equals("name"));
    }

    @Test
    public void testFindUserUpdate() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("valid@email.com");
        user.setName("name");
        user.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser = userStorage.create(user);

        createUser.setLogin("newLogin");
        createUser.setEmail("new@mail.com");

        userStorage.update(createUser);

        Optional<User> userOptional = userStorage.findById(createUser.getId());
        assertThat(userOptional).isPresent();
        User updatedUser = userOptional.get();
        assertThat(updatedUser.getLogin()).isEqualTo("newLogin");
        assertThat(updatedUser.getEmail()).isEqualTo("new@mail.com");

    }

    @Test
    public void testFindFilmUpdate() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1998, 2, 7));
        film.setDuration(120);
        film.setMpa(mpa);

        Film createFilm = filmStorage.create(film);

        createFilm.setName("newName");
        createFilm.setDescription("newDescription");

        filmStorage.update(createFilm);
        Optional<Film> filmOptional = filmStorage.findById(createFilm.getId());
        assertThat(filmOptional).isPresent();
        Film uptdatedFilm = filmOptional.get();
        assertThat(uptdatedFilm.getName()).isEqualTo("newName");
        assertThat(uptdatedFilm.getDescription()).isEqualTo("newDescription");
    }

    @Test
    public void testFindUserGetCommonFriends() {
        User user1 = new User();
        user1.setLogin("login1");
        user1.setEmail("valid1@email.com");
        user1.setName("name1");
        user1.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser1 = userStorage.create(user1);

        User user2 = new User();
        user2.setLogin("login2");
        user2.setEmail("valid2@email.com");
        user2.setName("name2");
        user2.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser2 = userStorage.create(user2);

        User user3 = new User();
        user3.setLogin("login3");
        user3.setEmail("valid3@email.com");
        user3.setName("name3");
        user3.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser3 = userStorage.create(user3);

        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());

        Set<User> user3Friends = userStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(user3Friends).hasSize(1);
        assertThat(user3Friends).contains(user3);
    }

    @Test
    public void testFindUserGetFriends() {
        User user1 = new User();
        user1.setLogin("login1");
        user1.setEmail("valid1@email.com");
        user1.setName("name1");
        user1.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser1 = userStorage.create(user1);

        User user2 = new User();
        user2.setLogin("login2");
        user2.setEmail("valid2@email.com");
        user2.setName("name2");
        user2.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser2 = userStorage.create(user2);

        userStorage.addFriend(createUser1.getId(), createUser2.getId());

        jdbcTemplate.update("UPDATE friends SET is_confirmed = true WHERE user_id = ? AND friend_id = ?",
                createUser1.getId(), createUser2.getId());

        Set<User> friends = userStorage.getFriends(createUser1.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends).contains(createUser2);
    }

    @Test
    public void testFindFilmAddLikeFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1998, 2, 7));
        film.setDuration(120);
        film.setMpa(mpa);
        Film createFilm = filmStorage.create(film);

        User user = new User();
        user.setLogin("login");
        user.setEmail("valid@email.com");
        user.setName("name");
        user.setBirthday(LocalDate.of(1998, 2, 7));
        User createUser = userStorage.create(user);

        filmStorage.addLikeFilm(createFilm.getId(), createUser.getId());

        List<Film> topFilms = filmStorage.getTopFilms(1);
        assertThat(topFilms).hasSize(1);
        assertThat(topFilms.get(0).getId()).isEqualTo(createFilm.getId());
    }

    @Test
    public void testFindFilmRemoveLike() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1998, 2, 7));
        film.setDuration(120);
        film.setMpa(mpa);
        Film createFilm = filmStorage.create(film);

        User user = new User();
        user.setLogin("login");
        user.setEmail("valid@email.com");
        user.setName("name");
        user.setBirthday(LocalDate.of(1998, 2, 7));
        User createUser = userStorage.create(user);

        filmStorage.addLikeFilm(createFilm.getId(), createUser.getId());

        filmStorage.removeLike(createFilm.getId(), createUser.getId());

        List<Film> topFilms = filmStorage.getTopFilms(1);
        assertThat(topFilms).hasSize(1);
        assertThat(topFilms.get(0).getId()).isEqualTo(createFilm.getId());
    }

    @Test
    public void testFindFilmGetTopFilms() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        User user1 = new User();
        user1.setLogin("login1");
        user1.setEmail("valid1@email.com");
        user1.setName("name1");
        user1.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser1 = userStorage.create(user1);

        User user2 = new User();
        user2.setLogin("login2");
        user2.setEmail("valid2@email.com");
        user2.setName("name2");
        user2.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser2 = userStorage.create(user2);

        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("description1");
        film1.setReleaseDate(LocalDate.of(1998, 2, 7));
        film1.setDuration(120);
        film1.setMpa(mpa);

        Film createFilm1 = filmStorage.create(film1);

        Film film2 = new Film();
        film2.setName("name2");
        film2.setDescription("description2");
        film2.setReleaseDate(LocalDate.of(1998, 2, 7));
        film2.setDuration(120);
        film2.setMpa(mpa);

        Film createFilm2 = filmStorage.create(film2);

        filmStorage.addLikeFilm(createFilm1.getId(), createUser1.getId());
        filmStorage.addLikeFilm(createFilm2.getId(), createUser2.getId());
        filmStorage.addLikeFilm(createFilm1.getId(), createUser2.getId());
        List<Film> topFilms = filmStorage.getTopFilms(10);

        assertThat(topFilms).hasSize(2);
        assertThat(topFilms.get(0).getId()).isEqualTo(film1.getId());
        assertThat(topFilms.get(1).getId()).isEqualTo(film2.getId());
    }

    @Test
    public void testFindUserDeleteFriend() {
        User user1 = new User();
        user1.setLogin("login1");
        user1.setEmail("valid1@email.com");
        user1.setName("name1");
        user1.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser1 = userStorage.create(user1);

        User user2 = new User();
        user2.setLogin("login2");
        user2.setEmail("valid2@email.com");
        user2.setName("name2");
        user2.setBirthday(LocalDate.of(1998, 2, 7));

        User createUser2 = userStorage.create(user2);

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.deleteFriend(user1.getId(), user2.getId());

        Set<User> friends = userStorage.getFriends(user1.getId());
        assertThat(friends).isEmpty();
    }
}

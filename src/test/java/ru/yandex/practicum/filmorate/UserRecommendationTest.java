package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserRecommendationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private LikeStorage likeStorage;

    private User user1;
    private User user2;
    private User user3;
    private Film film1;
    private Film film2;
    private Film film3;

    @BeforeEach
    public void setup() {
        User tempUser1 = User.builder()
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        user1 = userService.createUser(tempUser1);

        User tempUser2 = User.builder()
                .email("user2@example.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();
        user2 = userService.createUser(tempUser2);

        User tempUser3 = User.builder()
                .email("user3@example.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(1992, 3, 3))
                .build();
        user3 = userService.createUser(tempUser3);

        Film tempFilm1 = Film.builder()
                .name("Film One")
                .description("Description One")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .mpa(new MPA(1, "G"))
                .build();
        film1 = filmService.addFilm(tempFilm1);

        Film tempFilm2 = Film.builder()
                .name("Film Two")
                .description("Description Two")
                .releaseDate(LocalDate.of(2001, 2, 2))
                .duration(90L)
                .mpa(new MPA(2, "PG"))
                .build();
        film2 = filmService.addFilm(tempFilm2);

        Film tempFilm3 = Film.builder()
                .name("Film Three")
                .description("Description Three")
                .releaseDate(LocalDate.of(2002, 3, 3))
                .duration(110L)
                .mpa(new MPA(3, "PG-13"))
                .build();
        film3 = filmService.addFilm(tempFilm3);

        likeStorage.addLike(user1.getId(), film1.getId());
        likeStorage.addLike(user1.getId(), film2.getId());

        likeStorage.addLike(user2.getId(), film1.getId());
        likeStorage.addLike(user2.getId(), film2.getId());
        likeStorage.addLike(user2.getId(), film3.getId());

        likeStorage.addLike(user3.getId(), film1.getId());
    }

    @Test
    public void userExistsWithRecommendations() {
        List<Film> recommendations = userService.getRecommendations(user1.getId());

        Assertions.assertEquals(1, recommendations.size(), "Должна быть одна рекомендация");
        Assertions.assertEquals("Film Three", recommendations.get(0).getName(), "Рекомендованный фильм должен быть 'Film Three'");
    }

    @Test
    public void userExistsNoRecommendations() {
        likeStorage.addLike(user1.getId(), film3.getId());
        List<Film> recommendations = userService.getRecommendations(user1.getId());

        Assertions.assertTrue(recommendations.isEmpty(), "Рекомендации должны быть пустыми");
    }

    @Test
    public void userDoesNotExist() {
        Integer nonExistentUserId = 999;

        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getRecommendations(nonExistentUserId);
        }, "Должен быть выброшен NotFoundException для несуществующего пользователя");
    }

    @Test
    public void noSimilarUsers() {
        List<Film> recommendations = userService.getRecommendations(user3.getId());

        Assertions.assertEquals(2, recommendations.size(), "Должно быть две рекомендации");
        Assertions.assertTrue(recommendations.stream().anyMatch(film -> film.getName().equals("Film Two")));
        Assertions.assertTrue(recommendations.stream().anyMatch(film -> film.getName().equals("Film Three")));
    }

    @Test
    public void sameNumberOfCommonLikes() {
        likeStorage.deleteLike(user2.getId(), film2.getId());
        List<Film> recommendations = userService.getRecommendations(user1.getId());

        Assertions.assertEquals(1, recommendations.size(), "Должна быть одна рекомендация");
        Assertions.assertEquals("Film Three", recommendations.get(0).getName(), "Рекомендованный фильм должен быть 'Film Three'");
    }
}
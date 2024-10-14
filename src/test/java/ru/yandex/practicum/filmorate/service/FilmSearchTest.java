package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmSearchTest {
    private final FilmService filmService;
    private final DirectorService directorService;
    private final UserService userService;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;

    private Film film1;
    private Film film2;
    private Film film3;
    private Film film4;
    private Director director1;
    private Director director2;
    private Director director3;
    private User user1;

    @BeforeEach
    public void setup() {
        User tempUser1 = User.builder()
                .email("searchuser@example.com")
                .login("searchuser")
                .name("Search User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        user1 = userService.createUser(tempUser1);

        Director tempDirector1 = Director.builder()
                .name("Петр Петров")
                .build();
        director1 = directorService.addDirector(tempDirector1);

        Director tempDirector2 = Director.builder()
                .name("Иван Иванов")
                .build();
        director2 = directorService.addDirector(tempDirector2);

        Director tempDirector3 = Director.builder()
                .name("Иван Крадов")
                .build();
        director3 = directorService.addDirector(tempDirector3);

        Optional<Genre> optionalGenre1 = genreStorage.getById(1);
        Optional<Genre> optionalGenre2 = genreStorage.getById(2);

        if (optionalGenre1.isEmpty() || optionalGenre2.isEmpty()) {
            throw new BadRequestException("Необходимые жанры отсутствуют в базе данных");
        }

        Genre genre1 = optionalGenre1.get();
        Genre genre2 = optionalGenre2.get();

        Film tempFilm1 = Film.builder()
                .name("Крадущийся тигр, затаившийся дракон")
                .description("Фильм 1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120L)
                .mpa(new MPA(1, "G"))
                .genres(List.of(genre1))
                .directors(List.of(director1))
                .build();
        film1 = filmService.addFilm(tempFilm1);

        Film tempFilm2 = Film.builder()
                .name("Крадущийся в ночи")
                .description("Фильм 2")
                .releaseDate(LocalDate.of(2001, 2, 2))
                .duration(90L)
                .mpa(new MPA(2, "PG"))
                .genres(List.of(genre2))
                .directors(List.of(director2))
                .build();
        film2 = filmService.addFilm(tempFilm2);

        Film tempFilm3 = Film.builder()
                .name("Фильм 3")
                .description("Фильм 3")
                .releaseDate(LocalDate.of(2002, 3, 3))
                .duration(110L)
                .mpa(new MPA(3, "PG-13"))
                .genres(List.of(genre1, genre2))
                .directors(List.of(director1, director2))
                .build();
        film3 = filmService.addFilm(tempFilm3);

        Film tempFilm4 = Film.builder()
                .name("Фильм 4")
                .description("Фильм режиссёра с 'крад' в фамилии.")
                .releaseDate(LocalDate.of(2003, 4, 4))
                .duration(100L)
                .mpa(new MPA(4, "R"))
                .genres(List.of(genre1))
                .directors(List.of(director3))
                .build();
        film4 = filmService.addFilm(tempFilm4);

        likeStorage.addLike(user1.getId(), film1.getId());
        likeStorage.addLike(user1.getId(), film2.getId());
        likeStorage.addLike(user1.getId(), film3.getId());
        likeStorage.addLike(user1.getId(), film4.getId());
    }

    @Test
    public void searchByTitle() {
        String query = "крад";
        String by = "title";

        List<Film> results = filmService.searchFilms(query, by);

        Assertions.assertEquals(2, results.size(), "Должно быть два фильма, содержащих 'крад' в названии");
        Assertions.assertTrue(results.stream().anyMatch(film -> film.getName().equals("Крадущийся тигр, затаившийся дракон")));
        Assertions.assertTrue(results.stream().anyMatch(film -> film.getName().equals("Крадущийся в ночи")));
    }

    @Test
    public void searchByDirector() {
        String query = "крад";
        String by = "director";

        List<Film> results = filmService.searchFilms(query, by);

        Assertions.assertEquals(1, results.size(), "Должен быть один фильм, с режиссёром 'Иван Крадов'");
        Assertions.assertTrue(results.stream().anyMatch(film -> film.getName().equals("Фильм 4")));
    }

    @Test
    public void searchByTitleAndDirector() {
        String query = "крад";
        String by = "title,director";

        List<Film> results = filmService.searchFilms(query, by);

        Assertions.assertEquals(3, results.size(), "Должно быть три фильма");
        Assertions.assertTrue(results.stream().anyMatch(film -> film.getName().equals("Крадущийся тигр, затаившийся дракон")));
        Assertions.assertTrue(results.stream().anyMatch(film -> film.getName().equals("Крадущийся в ночи")));
        Assertions.assertTrue(results.stream().anyMatch(film -> film.getName().equals("Фильм 4")));
    }

    @Test
    public void searchNoResults() {
        String query = "несуществующий";
        String by = "title,director";

        List<Film> results = filmService.searchFilms(query, by);

        Assertions.assertTrue(results.isEmpty(), "Результаты поиска должны быть пустыми");
    }

    @Test
    public void searchInvalidByParameter() {
        String query = "крад";
        String by = "actor";

        Assertions.assertThrows(BadRequestException.class, () -> {
            filmService.searchFilms(query, by);
        }, "Должно выброситься исключение BadRequestException при неверном параметре 'by'");
    }

    @Test
    public void searchEmptyQuery() {
        String query = "";
        String by = "title";

        Assertions.assertThrows(BadRequestException.class, () -> {
            filmService.searchFilms(query, by);
        }, "Должно выброситься исключение BadRequestException при пустом запросе");
    }

    @Test
    public void searchNullQuery() {
        String query = null;
        String by = "director";

        Assertions.assertThrows(BadRequestException.class, () -> {
            filmService.searchFilms(query, by);
        }, "Должно выброситься исключение BadRequestException при null запросе");
    }

    @Test
    public void searchNullByParameter() {
        String query = "крад";
        String by = null;

        Assertions.assertThrows(BadRequestException.class, () -> {
            filmService.searchFilms(query, by);
        }, "Должно выброситься исключение BadRequestException при null параметре 'by'");
    }
}

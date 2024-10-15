package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FilmServiceTest {
    @Autowired
    private FilmService filmService;
    @Autowired
    private UserService userService;

    @Autowired
    private DirectorService directorService;

    private Film film1;
    private Film film2;
    private Film film3;
    private User user1;
    private User user2;
    private Director director1;
    private Director director2;

    @BeforeEach
    void setUp() {
        user1 = userService.createUser(new User(1, "mail1@mail.ru",
                "Логин1", "Имя1", LocalDate.of(1994, 11, 1), null));
        user2 = userService.createUser(new User(2, "mail2@mail.ru",
                "Логин2", "Имя2", LocalDate.of(2015, 11, 13), null));

        director1 = directorService.addDirector(new Director(1, "Режиссёр 1"));
        director2 = directorService.addDirector(new Director(2, "Режиссёр 2"));

        film1 = filmService.addFilm(new Film(1, null, "Фильм 1", "Описание фильма 1",
                LocalDate.of(1994, 1, 11), -3L,
                new MPA(1, "G"), null, List.of(director1)));
        film1.setLikes(List.of(1, 2));
        film2 = filmService.addFilm(new Film(2, null, "Фильм 2", "Описание фильма 2",
                LocalDate.of(2000, 4, 20), -3L,
                new MPA(2, "PG"), null, List.of(director2)));
        film2.setLikes(List.of(1));
        film3 = filmService.addFilm(new Film(3, null, "Фильм 3", "Описание фильма 3",
                LocalDate.of(1999, 4, 20), -3L,
                new MPA(2, "PG"), null, List.of(director1)));
        film3.setLikes(List.of(1));
        filmService.addLikeByUserIdAndFilmId(film1.getId(), user1.getId());
        filmService.addLikeByUserIdAndFilmId(film1.getId(), user2.getId());
        filmService.addLikeByUserIdAndFilmId(film2.getId(), user1.getId());
        filmService.addLikeByUserIdAndFilmId(film3.getId(), user1.getId());
    }

    @Test
    void testGetFilmsByDirectorSorterByLikes() {
        List<Film> cheking = List.of(filmService.getFilmById(film1.getId()), filmService.getFilmById(film3.getId()));
        List<Film> filmsByDirector = filmService.getFilmsByDirector(director1.getId(), "likes");
        assertEquals(cheking, filmsByDirector);
    }

    @Test
    void testGetFilmsByDirectorSorterByYear() {
        List<Film> cheking = List.of(filmService.getFilmById(film1.getId()), filmService.getFilmById(film3.getId()));
        List<Film> filmsByDirector = filmService.getFilmsByDirector(director1.getId(), "year");
        assertEquals(cheking, filmsByDirector);
    }
}

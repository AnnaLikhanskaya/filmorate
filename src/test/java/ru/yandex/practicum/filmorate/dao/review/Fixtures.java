package ru.yandex.practicum.filmorate.dao.review;

import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Fixtures {
    public static Film getFilm1() {
        return new Film(1,
                Collections.emptyList(),
                "Film 1",
                "Description 1",
                LocalDate.now(),
                100L,
                new MPA(1, "G"),
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                Collections.emptyList()
        );
    }

    public static Film getFilm2() {
        return new Film(2,
                Collections.emptyList(),
                "Film 2",
                "Description 2",
                LocalDate.now().minusDays(1),
                200L,
                new MPA(2, "PG"),
                Set.of(new Genre(3, "Мультфильм")),
                Collections.emptyList()
        );
    }

    public static Film getFilm3() {
        return new Film(3,
                Collections.emptyList(),
                "Film 3",
                "Description 3",
                LocalDate.now().minusDays(5),
                201L,
                new MPA(2, "PG"),
                Set.of(new Genre(3, "Мультфильм")),
                Collections.emptyList()
        );
    }

    public static User getUser1() {
        new User(1, "email", "login", "name", LocalDate.EPOCH, Collections.emptyList());
        return new User(1,
                "1@test.ru",
                "login1",
                "username1",
                LocalDate.now().minusYears(20),
                Collections.emptyList()
        );
    }

    public static User getUser2() {
        return new User(2,
                "2@test.ru",
                "login2",
                "username2",
                LocalDate.now().minusYears(15),
                Collections.emptyList()
        );
    }

    public static User getUser3() {
        return new User(3,
                "3@test.ru",
                "login3",
                "username3",
                LocalDate.now().minusYears(10),
                Collections.emptyList()
        );
    }

    public static Genre getGenre() {
        return getAllGenre().get(0);
    }

    public static List<Genre> getAllGenre() {
        return List.of(new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик"));
    }

    public static List<MPA> getAllMPA() {
        return List.of(new MPA(1, "G"),
                new MPA(2, "PG"),
                new MPA(3, "PG-13"),
                new MPA(4, "R"),
                new MPA(5, "NC-17"));
    }

    public static MPA getMPA() {
        return getAllMPA().get(0);
    }

    public static Review getReview(int userId, int filmId) {
        return new Review(1,
                "content",
                true,
                userId,
                filmId,
                0);
    }
}

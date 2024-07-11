package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    FilmController filmController;

    @Test
    public void createdFilmWithFailName() {
        Film filmNameIsBlank = Film.builder().id(0).name("    ").description("название фильма состоит из пробелов")
                .releaseDate(LocalDate.of(2000, 1, 4)).duration(100)
                .build();
        Film filmNameIsNull = Film.builder().id(1).name(null).description("название фильма = null")
                .releaseDate(LocalDate.of(2000, 2, 5)).duration(100)
                .build();
        Film filmNameIsEmpty = Film.builder().id(2).name("").description("имя фильма пустое")
                .releaseDate(LocalDate.of(2000, 3, 6)).duration(200)
                .build();
        Film filmNameIsCorrectly = Film.builder().id(3).name("название фильма").description("описание")
                .releaseDate(LocalDate.of(2000, 4, 7)).duration(10)
                .build();

        assertThrows(NullPointerException.class, () -> filmController.postFilm(filmNameIsBlank),
                "Ошибка тестирования проверки фильма с 'name', состоящим из пробелов.");

        assertThrows(NullPointerException.class, () -> filmController.postFilm(filmNameIsNull),
                "Ошибка тестирования проверки фильма с 'name' = null.");

        assertThrows(NullPointerException.class, () -> filmController.postFilm(filmNameIsEmpty),
                "Ошибка тестирования проверки фильма с пустым 'name'.");
        assertThrows(NullPointerException.class, () -> filmController.postFilm(filmNameIsCorrectly));

    }
}
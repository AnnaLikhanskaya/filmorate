package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Optional;


public class FilmValidation {

    public static void isExsistFilm(Optional<Film> film) {
        if (film.isEmpty())
            throw new NotFoundException("Фильм не найден");
    }

    public static void checkCorrectFilm(Film film) {
        if (film == null) throw new NotFoundException("Фильм отсутствует");
        if (film.getId() == null) throw new BadRequestException("Не корректный фильм");
    }

    public static void checkFilmDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Фильм не может быть выпущен раньше 28.12.1895");
        }
    }
}

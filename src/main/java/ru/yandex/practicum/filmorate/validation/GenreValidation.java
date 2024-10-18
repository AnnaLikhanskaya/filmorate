package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Optional;

public class GenreValidation {

    public static void isFoundGenre(Optional<Genre> genre) {
        if (genre.isEmpty()) {
            throw new NotFoundException("Жанр не найден");
        }
    }

    public static void isExsistGenre(Optional<Genre> genre) {
        if (genre.isEmpty()) {
            throw new BadRequestException("Жанр не найден");
        }
    }
}

package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Optional;

public class DirectorValidation {

    public static void validationDirector(Director director) {
        if (director.getName().isEmpty() || director.getName().isBlank()) {
            throw new BadRequestException("Имя режиссёра не должно быть пустым");
        }
    }

    public static void validationIsExsist(Optional<Director> director) {
        if (director.isEmpty()) {
            throw new NotFoundException("Режиссёр не найден");
        }
    }
}

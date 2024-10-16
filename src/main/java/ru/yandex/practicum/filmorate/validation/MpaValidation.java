package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Optional;

public class MpaValidation {

    public static void isExsistMpa(Optional<MPA> mpa) {
        if (mpa.isEmpty()) {
            throw new BadRequestException("Рейтинг не найден ");
        }
    }

    public static void isFoundMpa(Optional<MPA> mpa) {
        if (mpa.isEmpty()) {
            throw new NotFoundException("Рейтинг не найден ");
        }
    }
}

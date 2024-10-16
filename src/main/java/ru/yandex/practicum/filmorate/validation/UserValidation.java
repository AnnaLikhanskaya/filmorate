package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

public class UserValidation {

    public static void isExsistUser(Optional<User> user) {
        if (user.isEmpty())
            throw new NotFoundException("Пользователь не найден");
    }

    public static void validLogin(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }
}

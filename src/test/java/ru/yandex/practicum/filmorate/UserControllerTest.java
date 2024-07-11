package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    protected UserController userController;

    @Test
    public void createdUserWithFailName() {
        User userNameIsBlank = User.builder().id(0).email("    ").login("логин")
                .birthday(LocalDate.of(2000, 1, 4)).name("имя")
                .build();
        User userloginIsNull = User.builder().id(1).email("email").login(" ")
                .birthday(LocalDate.of(2000, 2, 5)).name("имя")
                .build();
        User userNameIsEmpty = User.builder().id(2).email("email").login("имя")
                .birthday(LocalDate.of(2000, 3, 6)).name("имя")
                .build();
        User userNameIsCorrectly = User.builder().id(3).email("email@mail.ru").login("login")
                .birthday(LocalDate.of(2000, 4, 7)).name("имя")
                .build();

        assertThrows(NullPointerException.class, () -> userController.getUsers(userNameIsBlank),
                "Ошибка тестирования проверки пользователя с 'email', состоящим из пробелов.");

        assertThrows(NullPointerException.class, () -> userController.getUsers(userloginIsNull),
                "Ошибка тестирования проверки фильма с 'login' = null.");

        assertThrows(NullPointerException.class, () -> userController.getUsers(userNameIsEmpty),
                "Ошибка тестирования проверки фильма с пустым 'name'.");
        assertThrows(NullPointerException.class, () -> userController.getUsers(userNameIsCorrectly));

    }
}
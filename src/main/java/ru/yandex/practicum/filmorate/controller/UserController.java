package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")

@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;
    private static final LocalDate DATA = LocalDate.now();

    public int createId() {
        return id++;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на список пользователей");
        return getListUsers();
    }

    private List<User> getListUsers() {
        Collection<User> value = users.values();
        return new ArrayList<>(value);
    }


    @PostMapping
    public User postUsers(@Valid @RequestBody User user) {
        try {
            validationUser(user);
            user.setId(createId());
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь " + user.getLogin() + " добавлен");
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        return user;
    }

    @PutMapping
    public User updateUsers(@Valid @RequestBody User user) {
        try {
            validationUser(user);
            if (users.containsKey(user.getId())) {
                if (user.getName() == null || user.getName().isBlank()) {
                    user.setName(user.getLogin());
                }
                users.replace(user.getId(), user);
                log.info("Пользователь " + user.getLogin() + " обновлён");
            } else {
                throw new ValidationException("Такого пользователя не существует");
            }
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        return user;
    }

    private void validationUser(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'");
        }
        if (user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(DATA)) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Component
public interface UserStorage {
    Collection<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(Integer userId);
}
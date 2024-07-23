package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    boolean contains(Integer id);
    List<User> getUser();
    User addUser(User user);
    void updateUser(User user);
    User getUserById(Integer id);
}
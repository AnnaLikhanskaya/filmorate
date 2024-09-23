package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NoExceptionObject;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;
    @Autowired
    private FriendStorage friendStorage;


    public Collection<User> getUsers() {
        log.info("Получен запрос на список всех пользователей");
        Collection<User> users = userStorage.getUsers();
        return users
                .stream()
                .peek(user -> user.setFriends(
                        friendStorage.getAllFriendsByUserId(user.getId())))
                .toList();
    }

    public User createUser(User user) {
        log.info("Получен запрос на создание пользавателя");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validLogin(user);
        User createdUser = userStorage.addUser(user);
        createdUser.setFriends(friendStorage.getAllFriendsByUserId(createdUser.getId()));
        return createdUser;
    }

    public User updateUser(User user) throws NoExceptionObject {
        log.info("Получен запрос на изменение пользователя");
        validLogin(user);
        User createdUser = userStorage.updateUser(user);
        createdUser.setFriends(friendStorage.getAllFriendsByUserId(createdUser.getId()));
        return createdUser;
    }

    public Optional<User> getUserById(int id) throws NoExceptionObject {
        log.info("Получен запрос на получение пользователя по ID");
        Optional<User> user = userStorage.getUserById(id);
        if (user.isEmpty()) throw new NotFoundException("Пользователь не найден с ID: " + id);
        user.get().setFriends(friendStorage.getAllFriendsByUserId(id));
        return user;
    }

    public Collection<User> findAllFriends(int id) {
        log.info("Получен запрос на список друзей пользователя");
        if (userStorage.getUserById(id).isEmpty())
            throw new NotFoundException("Пользователь не найден с ID: " + id);
        return friendStorage
                .getAllFriendsByUserId(id)
                .stream()
                .map(friendId -> userStorage.getUserById(friendId)
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + friendId)))
                .toList();

    }

    public void addToFriends(int id, int otherId) throws NoExceptionObject {
        log.info("Получен запрос на добавление в друзья");
        if (userStorage.getUserById(id).isEmpty()) throw new NotFoundException("Пользователь не найден с ID: " + id);
        if (userStorage.getUserById(otherId).isEmpty())
            throw new NotFoundException("Пользователь не найден с ID: " + otherId);
        friendStorage.addFriend(id, otherId);
    }


    public void removeFromFriends(int id, int otherId) {
        log.info("Получен запрос на удаление из друзей");
        if (userStorage.getUserById(id).isEmpty()) throw new NotFoundException("Пользователь не найден с ID: " + id);
        if (userStorage.getUserById(otherId).isEmpty())
            throw new NotFoundException("Пользователь не найден с ID: " + otherId);
        friendStorage.deleteFriend(id, otherId);
    }

    public Collection<User> getMutualFriends(Integer id, Integer otherId) {
        log.info("Получен запрос на получение списка общих друзей");
        Optional<User> optionalUser = userStorage.getUserById(id);
        Optional<User> optionalFriend = userStorage.getUserById(otherId);
        if (optionalUser.isEmpty()) throw new NotFoundException("Пользователь не найден с ID: " + id);
        if (optionalFriend.isEmpty()) throw new NotFoundException("Пользователь не найден с ID: " + otherId);
        User user = optionalUser.get();
        User friend = optionalFriend.get();
        user.setFriends(friendStorage.getAllFriendsByUserId(user.getId()));
        friend.setFriends(friendStorage.getAllFriendsByUserId(friend.getId()));
        if (user.getFriends().isEmpty() || friend.getFriends().isEmpty()) return new ArrayList<>();
        return user.getFriends().stream()
                .filter(friendId -> friend.getFriends().contains(friendId))
                .map(friendId -> userStorage.getUserById(friendId)
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + friendId)))
                .toList();
    }

    private void validLogin(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }
}




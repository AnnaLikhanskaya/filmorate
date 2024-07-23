package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoExceptionObject;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public List<User> getUser() {
        return storage.getUser();
    }

    private void setUserNameIfNeeded(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public User createUser(User user) {
        checkLoginOnSpace(user.getLogin());
        setUserNameIfNeeded(user);
        return storage.addUser(user);
    }

    private void checkLoginOnSpace(String login) {
        if(login.contains(" ")){
            throw new ValidationException("Логин не должен содержать пробелы");
        }
    }

    public void updateUser(User user) throws NoExceptionObject {
        checkLoginOnSpace(user.getLogin());
        storage.updateUser(user);
    }

    public User getUserById(Integer id) throws NoExceptionObject {
        return storage.getUserById(id);
    }

    public List<User> findAllFriends(Integer id) {
        List<User> users = new ArrayList<>();
        for (Integer friendId : storage.getUserById(id).getFriends()) {
            users.add(storage.getUserById(friendId));
        }
        return users;
    }

    public void addToFriends(Integer id, Integer otherId) throws NoExceptionObject {
        User user = storage.getUserById(id);
        User friend = storage.getUserById(otherId);
        user.addFriend(otherId);
        friend.addFriend(id);
    }


    public void removeFromFriends(Integer id, Integer otherId) {
        User user = storage.getUserById(id);
        User friend = storage.getUserById(otherId);
        user.deleteFriend(otherId);
        friend.deleteFriend(id);
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {
        User user = storage.getUserById(id);
        User friend = storage.getUserById(otherId);
        if (user.getFriends() != null && friend.getFriends() != null) {
            return user.getFriends().stream()
                    .map(storage::getUserById)
                    .filter(u -> friend.getFriends().contains(u.getId()))
                    .collect(toList());
        } else {
            return new ArrayList<>();
        }
    }
}





package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendStorage;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NoExceptionObject;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Service
public class UserService {

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;
    @Autowired
    private FriendStorage friendStorage;
    @Autowired
    private LikeStorage likeStorage;
    @Autowired
    private FilmService filmService;


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

    public List<Film> getRecommendations(Integer userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден с ID: " + userId);
        }

        List<Integer> userLikedFilms = likeStorage.getUserLikes(userId);

        Collection<User> allUsers = userStorage.getUsers();
        allUsers.removeIf(user -> user.getId().equals(userId));

        if (allUsers.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, List<Integer>> otherUsersLikes = new HashMap<>();
        for (User user : allUsers) {
            List<Integer> likedFilms = likeStorage.getUserLikes(user.getId());
            otherUsersLikes.put(user.getId(), likedFilms);
        }

        int maxCommonLikes = 0;
        List<Integer> bestUserIds = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : otherUsersLikes.entrySet()) {
            Integer otherUserId = entry.getKey();
            List<Integer> otherUserLikedFilms = entry.getValue();

            Set<Integer> commonLikes = new HashSet<>(userLikedFilms);
            commonLikes.retainAll(otherUserLikedFilms);
            int commonLikesSize = commonLikes.size();

            if (commonLikesSize > maxCommonLikes) {
                maxCommonLikes = commonLikesSize;
                bestUserIds.clear();
                bestUserIds.add(otherUserId);
            } else if (commonLikesSize == maxCommonLikes) {
                bestUserIds.add(otherUserId);
            }
        }

        if (maxCommonLikes == 0) {
            return new ArrayList<>();
        }

        Set<Integer> recommendationsFilmIds = new HashSet<>();
        for (Integer bestUserId : bestUserIds) {
            List<Integer> bestUserLikedFilms = otherUsersLikes.get(bestUserId);
            for (Integer filmId : bestUserLikedFilms) {
                if (!userLikedFilms.contains(filmId)) {
                    recommendationsFilmIds.add(filmId);
                }
            }
        }

        List<Film> recommendations = new ArrayList<>();
        for (Integer filmId : recommendationsFilmIds) {
            Film film = filmService.getFilmById(filmId);
            recommendations.add(film);
        }
        return recommendations;
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
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

import static ru.yandex.practicum.filmorate.validation.UserValidation.isExsistUser;
import static ru.yandex.practicum.filmorate.validation.UserValidation.validLogin;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final LikeStorage likeStorage;
    private final FilmService filmService;

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
        user = setLoginName(user);
        validLogin(user);
        User createdUser = userStorage.addUser(user);
        createdUser.setFriends(friendStorage.getAllFriendsByUserId(createdUser.getId()));
        return createdUser;
    }

    public User updateUser(User user) throws NoExceptionObject {
        log.info("Получен запрос на изменение пользователя");
        user = setLoginName(user);
        validLogin(user);
        User createdUser = userStorage.updateUser(user);
        createdUser.setFriends(friendStorage.getAllFriendsByUserId(createdUser.getId()));
        return createdUser;
    }

    public Optional<User> getUserById(int id) throws NoExceptionObject {
        log.info("Получен запрос на получение пользователя по ID");
        Optional<User> user = userStorage.getUserById(id);
        isExsistUser(user);
        user.get().setFriends(friendStorage.getAllFriendsByUserId(id));
        return user;
    }

    public Collection<User> findAllFriends(int id) {
        log.info("Получен запрос на список друзей пользователя");
        isExsistUser(userStorage.getUserById(id));
        return friendStorage
                .getAllFriendsByUserId(id)
                .stream()
                .map(friendId -> {
                    isExsistUser(userStorage.getUserById(friendId));
                    return userStorage.getUserById(friendId).get();
                })
                .toList();
    }

    public void addToFriends(int id, int otherId) {
        log.info("Получен запрос на добавление в друзья");
        isExsistUser(userStorage.getUserById(id));
        isExsistUser(userStorage.getUserById(otherId));
        friendStorage.addFriend(id, otherId);
    }


    public void removeFromFriends(int id, int otherId) {
        log.info("Получен запрос на удаление из друзей");
        isExsistUser(userStorage.getUserById(id));
        isExsistUser(userStorage.getUserById(otherId));
        friendStorage.deleteFriend(id, otherId);
    }

    public Collection<User> getMutualFriends(Integer id, Integer otherId) {
        log.info("Получен запрос на получение списка общих друзей");
        Optional<User> optionalUser = userStorage.getUserById(id);
        Optional<User> optionalFriend = userStorage.getUserById(otherId);
        isExsistUser(userStorage.getUserById(id));
        isExsistUser(userStorage.getUserById(otherId));
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


    private User getUserOrThrow(Integer userId) {
        Optional<User> user = userStorage.getUserById(userId);
        isExsistUser(user);
        return user.get();
    }

    private Collection<User> getOtherUsers(Integer userId) {
        Collection<User> allUsers = userStorage.getUsers();
        allUsers.removeIf(user -> user.getId().equals(userId));
        return allUsers;
    }

    private Map<Integer, List<Integer>> getOtherUsersLikes(Collection<User> otherUsers) {
        Map<Integer, List<Integer>> otherUsersLikes = new HashMap<>();
        for (User user : otherUsers) {
            List<Integer> likedFilms = likeStorage.getUserLikes(user.getId());
            otherUsersLikes.put(user.getId(), likedFilms);
        }
        return otherUsersLikes;
    }

    private List<Integer> findBestUserIds(List<Integer> userLikedFilms, Map<Integer, List<Integer>> otherUsersLikes) {
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
        return maxCommonLikes > 0 ? bestUserIds : Collections.emptyList();
    }

    private User setLoginName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    public List<Film> getRecommendations(Integer userId) {

        if (userId == null) {
            throw new ValidationException("userId не может быть null");
        }

        User user = getUserOrThrow(userId);
        List<Integer> userLikedFilms = likeStorage.getUserLikes(userId);

        if (userLikedFilms == null) {
            userLikedFilms = Collections.emptyList();
        }

        Collection<User> otherUsers = getOtherUsers(userId);

        if (otherUsers == null || otherUsers.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, List<Integer>> otherUsersLikes = getOtherUsersLikes(otherUsers);

        if (otherUsersLikes == null || otherUsersLikes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> bestUserIds = findBestUserIds(userLikedFilms, otherUsersLikes);

        if (bestUserIds == null || bestUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> recommendationsFilmIds = filmService.collectRecommendations(bestUserIds, otherUsersLikes, userLikedFilms);

        if (recommendationsFilmIds == null || recommendationsFilmIds.isEmpty()) {
            return Collections.emptyList();
        }

        return filmService.getFilmsByIds(recommendationsFilmIds);
    }
}
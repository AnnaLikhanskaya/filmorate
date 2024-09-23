//package ru.yandex.practicum.filmorate.dao.user;
//
//import ru.yandex.practicum.filmorate.exception.NoExceptionObject;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.dao.UserStorage;
//
//import java.util.*;
//
//
//public class InMemoryUserStorage implements UserStorage {
//    private final Map<Integer, User> users = new HashMap<>();
//    private Integer id = 1;
//
//    private int createId() {
//        return id++;
//    }
//
//    public boolean contains(Integer id) {
//        return users.containsKey(id);
//    }
//
//    @Override
//    public Collection<User> getUsers() {
//        return new ArrayList<>(users.values());
//    }
//
//    @Override
//    public User addUser(User user) {
//        user.setId(createId());
//        users.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public User updateUser(User user) {
//        if (users.containsKey(user.getId())) {
//            users.put(user.getId(), user);
//        } else {
//            throw new NoExceptionObject("Такого пользователя не существует");
//        }
//        return user;
//    }
//
//
//    @Override
//    public void addFriend(Integer firstUserLogin, Integer secondUserLogin) {
//
//    }
//
//    @Override
//    public Collection<User> getFriendList(Integer id) {
//        return List.of();
//    }
//
//    @Override
//    public void deleteFriend(Integer firstUserId, Integer secondUserId) {
//
//    }
//    @Override
//    public Optional<User> getUserById(Integer id) {
//        return Optional.empty();
//    }
//
//    @Override
//    public Collection<User> getMutualFriends(Integer id, Integer otherId) {
//        return List.of();
//    }
//
//
//}

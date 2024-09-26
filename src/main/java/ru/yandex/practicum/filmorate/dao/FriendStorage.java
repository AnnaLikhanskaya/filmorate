package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;
import java.util.List;

public interface FriendStorage {
    List<Integer> addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<Integer> getAllFriendsByUserId(int userId);

}

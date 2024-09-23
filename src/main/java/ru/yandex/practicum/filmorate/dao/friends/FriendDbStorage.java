package ru.yandex.practicum.filmorate.dao.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.FriendStorage;
import ru.yandex.practicum.filmorate.mappers.FriendsRowMapper;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FriendDbStorage extends BaseRepository<Friend> implements FriendStorage {

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbc, FriendsRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Integer> getAllFriendsByUserId(int userId) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE USER_ID = ?";
        return super.findMany(sqlQuery, userId).stream()
                .map(Friend::getFriendId)
                .collect(Collectors.toList());

    }

    @Override
    public List<Integer> addFriend(Integer userId, Integer friendId) {
        super.insert("INSERT INTO FRIENDS(user_id, friend_id) values ( ?,? );",
                userId,
                friendId);
        return this.getAllFriendsByUserId(userId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        super.delete("DELETE FROM FRIENDS where USER_ID = ? and FRIEND_ID = ?",
                userId, friendId);
    }
}
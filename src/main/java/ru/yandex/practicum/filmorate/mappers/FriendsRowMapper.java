package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendsRowMapper implements RowMapper<Friend> {

    @Override
    public Friend mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friend friend = new Friend();
        friend.setId(rs.getInt("ID"));
        friend.setUserId(rs.getInt("USER_ID"));
        friend.setFriendId(rs.getInt("FRIEND_ID"));
        return friend;
    }
}

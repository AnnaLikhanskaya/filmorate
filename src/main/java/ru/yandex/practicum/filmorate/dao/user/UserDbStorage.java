package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User addUser(User user) {
        super.insert(
                "INSERT INTO USERS (name, email, login, birthday)" +
                        " values (?, ?, ?, ?)",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday());
        Integer id = jdbc.queryForObject("SELECT ID FROM users where email = ?",
                Integer.class, user.getEmail());
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        super.update("UPDATE USERS SET " +
                        "NAME = ?, " +
                        "EMAIL = ?, " +
                        "LOGIN = ?, " +
                        "BIRTHDAY = ? " +
                        "WHERE ID = ?",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return super.findOne("SELECT * FROM users where ID = ?", userId);
    }

    @Override
    public Collection<User> getUsers() {
        return super.findMany("SELECT * FROM users");
    }
}
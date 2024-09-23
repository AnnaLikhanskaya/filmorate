package ru.yandex.practicum.filmorate.dao.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Repository
public class LikeDbStorage extends BaseRepository<Like> implements LikeStorage {

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbc, LikeRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addLike(int userId, int filmId) {
        String sqlQuery = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) " +
                "VALUES(?, ?)";
        super.insert(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int userId, int filmId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? " +
                "AND USER_ID = ?";
        super.delete(sqlQuery, userId, filmId);
    }

    public List<Integer> getFilmLikes(int filmId) {
        String sqlQuery = "SELECT * FROM FILM_LIKES WHERE film_id = ?";
        List<Like> likes = super.findMany(sqlQuery, filmId);
        return likes.stream()
                .map(Like::getId).toList();
    }
}
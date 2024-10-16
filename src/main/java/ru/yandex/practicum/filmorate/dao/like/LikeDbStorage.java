package ru.yandex.practicum.filmorate.dao.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class LikeDbStorage extends BaseRepository<Like> implements LikeStorage {

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbc, LikeRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) " +
                "VALUES(?, ?)";
        super.insert(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? " +
                "AND USER_ID = ?";
        super.delete(sqlQuery, filmId, userId);
    }

    public List<Integer> getFilmLikes(int filmId) {
        String sqlQuery = "SELECT * FROM FILM_LIKES WHERE film_id = ?";
        List<Like> likes = super.findMany(sqlQuery, filmId);
        return likes.stream()
                .map(Like::getId).toList();
    }

    @Override
    public HashMap<Integer, List<Integer>> getAllLikesByFilmId() {
        String likesSqlQuery = "SELECT * FROM FILM_LIKES";
        List<Like> likes = super.findMany(likesSqlQuery);

        HashMap<Integer, List<Integer>> likesByFilmsId = new HashMap<>();

        for (Like like : likes) {
            if (!likesByFilmsId.containsKey(like.getFilmId())) {
                likesByFilmsId.put(like.getFilmId(), new ArrayList<>());

            }
            likesByFilmsId.get(like.getFilmId()).add(like.getId());

        }

        return likesByFilmsId;
    }

    @Override
    public List<Integer> getUserLikes(int userId) {
        String sqlQuery = "SELECT film_id FROM film_likes WHERE user_id = ?";
        return jdbc.queryForList(sqlQuery, Integer.class, userId);
    }
}
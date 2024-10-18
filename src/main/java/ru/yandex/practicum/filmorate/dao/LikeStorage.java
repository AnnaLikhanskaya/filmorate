package ru.yandex.practicum.filmorate.dao;

import java.util.HashMap;
import java.util.List;

public interface LikeStorage {
    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Integer> getFilmLikes(int filmId);

    HashMap<Integer, List<Integer>> getAllLikesByFilmId();

    List<Integer> getUserLikes(int userId);
}
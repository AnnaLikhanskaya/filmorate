package ru.yandex.practicum.filmorate.dao;

import java.util.HashMap;
import java.util.List;

public interface LikeStorage {
    void addLike(int userId, int filmId);

    void deleteLike(int userId, int filmId);

    List<Integer> getFilmLikes(int filmId);

    HashMap<Integer, List<Integer>> getAllLikesByFilmId();
}
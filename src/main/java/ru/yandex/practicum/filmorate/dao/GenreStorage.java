package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public interface GenreStorage {
    Map<Integer, Set<Genre>> getAllGenresByFilmId();

    List<Genre> getAll();

    Optional<Genre> getById(Integer id);

    Set<Genre> getByFilmId(Integer filmId);

    void assignGenre(Integer filmId, Integer genreId);

    void deleteGenresByFilmId(Integer filmId);
}
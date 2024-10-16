package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getDirectors();

    Director addDirector(Director director);

    Optional<Director> getById(Integer id);

    Director updateDirector(Director director);

    void deleteDirector(Integer id);

    Map<Integer, List<Director>> getAllDirectorsByFilmId();

    List<Director> getByFilmId(Integer id);

    void deleteDirectorsByFilmId(Integer id);

    void assignDirector(Integer filmId, Integer directorId);
}

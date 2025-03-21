package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public interface FilmStorage {

    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilms(Film film);

    Optional<Film> getFilmById(Integer filmId);

    Set<Film> getFilmsByUserId(Integer userId);

    List<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector);

    List<Film> getPopular(Integer count, Integer genreId, Integer year);

    void deleteFilmById(Integer filmId);

    List<Film> getFilmsByDirectorId(Integer directorId);
}
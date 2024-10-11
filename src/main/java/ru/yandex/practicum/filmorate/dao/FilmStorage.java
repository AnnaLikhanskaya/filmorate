package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Component
public interface FilmStorage {

    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilms(Film film);

    Optional<Film> getFilmById(Integer filmId);

    List<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector);

}
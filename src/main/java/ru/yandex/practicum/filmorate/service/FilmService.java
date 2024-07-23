package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoExceptionObject;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class FilmService {
    @Autowired
    private final FilmStorage storage;

    @Autowired
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return storage.getFilms();
    }

    public Film releaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Фильм не может быть выпущен раньше 28.12.1895");
        }
        return film;
    }

    public Film addFilm(Film film) {
        releaseDate(film);
        return storage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        releaseDate(film);
        return storage.updateFilms(film);
    }

    public Film getFilmById(Integer id) {
        return storage.getFilmById(id);
    }

    public void addLike(Integer id, Integer userId) {
        if(!userStorage.contains(userId)){
            throw new NoExceptionObject("Такой пользователь не существует");
        }
        storage.getFilmById(id).addLike(userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        if (storage.getFilmById(id).getLikes().contains(userId)) {
            storage.getFilmById(id).deleteLike(userId);
        } else {
            throw new NoExceptionObject("Такой пользователь не оставлял лайк на этот фильм");
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        return storage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getCountLikes).reversed())
                .limit(count)
                .collect(toList());

    }
}

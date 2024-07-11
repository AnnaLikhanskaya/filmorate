package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")

public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;
    private static final LocalDate RELEASE_DATE = LocalDate.of(1985, 12, 28);

    public int createId() {
        return nextId++;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на список фильмов");
        return getListFilms();
    }

    private List<Film> getListFilms() {
        Collection<Film> value = films.values();
        return new ArrayList<>(value);
    }


    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        try {
            validationFilm(film);
            film.setId(createId());
            films.put(film.getId(), film);
            log.info("Ваш фильм добавлен");
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            validationFilm(film);
            if (films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
                log.info("Ваш фильм обновлен");
            } else {
                throw new RuntimeException("Такого фильма не существует");
            }
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        return film;
    }

    private void validationFilm(Film film) throws ValidationException {
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года;" + film.getReleaseDate());
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}

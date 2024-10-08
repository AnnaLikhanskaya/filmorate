package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/films")
@Qualifier("FilmDbStorage")
public class FilmController {
    @Autowired
    private FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на список фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        Film added = filmService.addFilm(film);
        log.info("Ваш фильм добавлен " + film.getId() + ". Фильм называется " + film.getName());
        return added;
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Ваш фильм" + film.getName() + "обновлён");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Фильму {} поставлен Лайк", id);
        filmService.addLikeByUserIdAndFilmId(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("У фильма {} удален лайк", id);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен запрос на список популярных фильмов");
        return filmService.getPopularFilms(count);
    }
}
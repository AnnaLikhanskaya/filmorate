package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Min(1) int count,
                                 @RequestParam(required = false) @Min(1) Integer genreId,
                                 @RequestParam(required = false) @Min(1895) Integer year) {
        log.info("Получен запрос на список популярных фильмов");
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable Integer directorId,
                                         @RequestParam(required = false, defaultValue = "year")
                                         @Pattern(regexp = "^(year|likes)$",
                                                 message = "Не корректный тип сортировки") String sortBy) {

        log.info("Получен запрос на список фильмов у режиссёра: " + directorId);
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable Integer filmId){
        log.info("Получен запрос на удаление фильма с id: {}", filmId);
        filmService.deleteFilm(filmId);
    }
}
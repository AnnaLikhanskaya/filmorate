package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmsStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage storage,
                       @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("MPADbStorage") MpaStorage mpaStorage) {
        this.filmsStorage = storage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public List<Film> getAllFilms() {
        log.info("Получен запрос на список всех фильмов");
        List<Film> films = filmsStorage.getFilms();
        Map<Integer, List<Integer>> likes = likeStorage.getAllLikesByFilmId();
        Map<Integer, List<Genre>> genres = genreStorage.getAllGenresByFilmId();
        Map<Integer, MPA> mpa = mpaStorage.getAll();
        List<Film> list = new ArrayList<>();
        for (Film film : films) {
            film.setLikes(likes.get(film.getId()));
            film.setGenres(genres.get(film.getId()));
            film.setMpa(mpa.get(film.getMpa().getId()));
            list.add(film);
        }
        return list;

    }

    public Film addFilm(Film film) {
        log.info("Получен запрос на добавление фильма");
        checkFilmDate(film);
        Optional<MPA> mpa = mpaStorage.getById(film.getMpa().getId());
        if (mpa.isEmpty()) {
            throw new BadRequestException("Рейтинг не найден " + film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            film.setGenres(film.getGenres().stream().distinct().toList());
            film.getGenres()
                    .forEach(genre -> {
                        Optional<Genre> genreFromStorage = genreStorage.getById(genre.getId());
                        if (genreFromStorage.isEmpty()) {
                            throw new BadRequestException("Жанр не найден " + genre.getId());
                        }
                    });
        }


        Film createdFilm = filmsStorage.addFilm(film);
        assignGenresToFilm(film.getGenres(), createdFilm.getId());
        return setFilmData(createdFilm);
    }

    public Film updateFilm(Film film) {
        log.info("Получен запрос на обнавление фильма");
        checkFilmDate(film);
        genreStorage.deleteGenresByFilmId(film.getId());
        assignGenresToFilm(film.getGenres(), film.getId());
        return setFilmData(filmsStorage.updateFilms(film));
    }

    public Film getFilmById(Integer id) {
        log.info("Получен запрос на получение фильма по ID");
        Optional<Film> film = filmsStorage.getFilmById(id);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        return setFilmData(film.get());
    }

    public void addLikeByUserIdAndFilmId(Integer id, Integer userId) {
        log.info("Получен запрос добавления лайка");
        likeStorage.addLike(userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        log.info("Получен запрос на удаление лайка");
        likeStorage.deleteLike(userId, id);
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Получен запрос на список популярных фильмов");
        List<Film> films = filmsStorage.getFilms();
        films.forEach(this::setFilmData);
        return films.stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void assignGenresToFilm(List<Genre> genres, int filmId) {
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> genreStorage.assignGenre(filmId, genre.getId()));
        }
    }

    private Film setFilmData(Film film) {
        film.setGenres(genreStorage.getByFilmId(film.getId()));
        film.setLikes(likeStorage.getFilmLikes(film.getId()));
        film.setMpa(mpaStorage.getById(film.getMpa().getId()).orElseGet(null));
        return film;
    }

    private static void checkFilmDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Фильм не может быть выпущен раньше 28.12.1895");
        }
    }
}

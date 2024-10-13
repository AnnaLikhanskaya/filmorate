package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.yandex.practicum.filmorate.validation.DirectorValidation.validationIsExsist;
import static ru.yandex.practicum.filmorate.validation.FilmValidation.*;
import static ru.yandex.practicum.filmorate.validation.GenreValidation.isExsistGenre;
import static ru.yandex.practicum.filmorate.validation.MpaValidation.isExsistMpa;
import static ru.yandex.practicum.filmorate.validation.UserValidation.isExsistUser;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmsStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;


    public List<Film> getAllFilms() {
        log.info("Получен запрос на список всех фильмов");
        List<Film> films = filmsStorage.getFilms();
        return fillMovieData(films);

    }

    public Film addFilm(Film film) {
        log.info("Получен запрос на добавление фильма");
        checkFilmDate(film);
        Optional<MPA> mpa = mpaStorage.getById(film.getMpa().getId());
        isExsistMpa(mpa);
        if (film.getGenres() != null) {
            film.setGenres(film.getGenres().stream().distinct().toList());
            film.getGenres()
                    .forEach(genre -> {
                        Optional<Genre> genreFromStorage = genreStorage.getById(genre.getId());
                        isExsistGenre(genreFromStorage);
                    });
        }
        if (film.getDirectors() != null) {
            film.setDirectors(film.getDirectors().stream().distinct().toList());
            film.getDirectors()
                    .forEach(director -> {
                        Optional<Director> directorFromStorage = directorStorage.getById(director.getId());
                        validationIsExsist(directorFromStorage);
                    });
        }
        Film createdFilm = filmsStorage.addFilm(film);
        assignGenresToFilm(film.getGenres(), createdFilm.getId());
        assignDirectorsToFilm(film.getDirectors(), createdFilm.getId());
        return setFilmData(createdFilm);
    }

    public Film updateFilm(Film film) {
        checkCorrectFilm(film);
        log.info("Получен запрос на обновление фильма");
        checkFilmDate(film);
        genreStorage.deleteGenresByFilmId(film.getId());
        directorStorage.deleteDirectorsByFilmId(film.getId());
        if (film.getGenres() != null) assignGenresToFilm(film.getGenres(), film.getId());
        if (film.getDirectors() != null) assignDirectorsToFilm(film.getDirectors(), film.getId());
        return setFilmData(filmsStorage.updateFilms(film));
    }


    public Film getFilmById(Integer id) {
        log.info("Получен запрос на получение фильма по ID");
        Optional<Film> film = filmsStorage.getFilmById(id);
        isExsistFilm(film);
        return setFilmData(film.get());
    }

    public void addLikeByUserIdAndFilmId(Integer filmId, Integer userId) {
        log.info("Получен запрос добавления лайка");
        isExsistFilm(filmsStorage.getFilmById(filmId));
        isExsistUser(userStorage.getUserById(userId));
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.info("Получен запрос на удаление лайка");
        isExsistFilm(filmsStorage.getFilmById(filmId));
        isExsistUser(userStorage.getUserById(userId));
        likeStorage.deleteLike(filmId, userId);
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

    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        validationIsExsist(directorStorage.getById(directorId));
        List<Film> allFilms = getAllFilms();
        Stream<Film> directorFilms = allFilms.stream().filter(film ->
                film.getDirectors() != null && film.getDirectors().stream()
                        .map(Director::getId).toList().contains(directorId));

        if (sortBy.equals("likes")) {
            return directorFilms
                    .sorted(Comparator.comparing(film -> ((Film) film).getLikes().size()).reversed()).toList();
        }
        return directorFilms.sorted(Comparator.comparing(Film::getReleaseDate)).toList();
    }

    private void assignDirectorsToFilm(List<Director> directors, Integer id) {
        if (directors != null && !directors.isEmpty()) {
            directors.forEach(director -> directorStorage.assignDirector(id, director.getId()));
        }
    }

    private void assignGenresToFilm(List<Genre> genres, int filmId) {
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> genreStorage.assignGenre(filmId, genre.getId()));
        }
    }

    private Film setFilmData(Film film) {
        checkCorrectFilm(film);
        film.setGenres(genreStorage.getByFilmId(film.getId()));
        film.setLikes(likeStorage.getFilmLikes(film.getId()));
        film.setMpa(mpaStorage.getById(film.getMpa().getId()).orElseGet(null));
        film.setDirectors(directorStorage.getByFilmId(film.getId()));
        return film;
    }


    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        log.info("Попытка получить общие фильмы userId={}, friendId={}.", userId, friendId);
        isExsistUser(userStorage.getUserById(userId));
        isExsistUser(userStorage.getUserById(friendId));
        Set<Film> userFilms = filmsStorage.getFilmsByUserId(userId);
        Set<Film> friendFilms = filmsStorage.getFilmsByUserId(friendId);
        userFilms.retainAll(friendFilms);

        log.info("Общие фильмы успешно получены userId={}, friendId={}.", userId, friendId);
        return fillMovieData(userFilms.stream()
                .sorted(Comparator.comparingInt(film -> ((Film) film).getLikes().size()).reversed())
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    public List<Film> fillMovieData(List<Film> filmList) {
        Map<Integer, List<Integer>> likes = likeStorage.getAllLikesByFilmId();
        Map<Integer, List<Genre>> genres = genreStorage.getAllGenresByFilmId();
        Map<Integer, MPA> mpa = mpaStorage.getAll();
        Map<Integer, List<Director>> directors = directorStorage.getAllDirectorsByFilmId();
        List<Film> list = new ArrayList<>();
        for (Film film : filmList) {
            Integer filmId = film.getId();
            film.setLikes(likes.get(filmId));
            if (film.getLikes() == null) film.setLikes(new ArrayList<>());
            film.setGenres(genres.get(filmId));
            if (film.getGenres() == null) film.setGenres(new ArrayList<>());
            film.setMpa(mpa.get(film.getMpa().getId()));
            film.setDirectors(directors.get(filmId));
            if (film.getDirectors() == null) film.setDirectors(new ArrayList<>());
            list.add(film);
        }
        return list;

    }

    public List<Film> getFilmsByIds(Set<Integer> filmIds) {
        List<Film> recommendations = new ArrayList<>();
        for (Integer filmId : filmIds) {
            Film film = getFilmById(filmId);
            recommendations.add(film);
        }
        return recommendations;
    }

    public Set<Integer> collectRecommendations(List<Integer> bestUserIds, Map<Integer, List<Integer>> otherUsersLikes,
                                               List<Integer> userLikedFilms) {
        Set<Integer> recommendationsFilmIds = new HashSet<>();
        for (Integer bestUserId : bestUserIds) {
            List<Integer> bestUserLikedFilms = otherUsersLikes.get(bestUserId);
            for (Integer filmId : bestUserLikedFilms) {
                if (!userLikedFilms.contains(filmId)) {
                    recommendationsFilmIds.add(filmId);
                }
            }
        }
        return recommendationsFilmIds;
    }
}

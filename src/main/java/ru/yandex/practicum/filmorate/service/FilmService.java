package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validation.DirectorValidation.validationIsExsist;
import static ru.yandex.practicum.filmorate.validation.FilmValidation.checkCorrectFilm;
import static ru.yandex.practicum.filmorate.validation.FilmValidation.isExsistFilm;
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
    private final EventStorage eventStorage;


    private static void checkFilmDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Фильм не может быть выпущен раньше 28.12.1895");
        }
    }

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
            Set<Genre> genres = film.getGenres();
            film.setGenres(genres);
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

        eventStorage.addEvent(Event.builder()
                .timestamp(Instant.now())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(EventOperation.ADD)
                .entityId(filmId)
                .build());
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.info("Получен запрос на удаление лайка");

        isExsistFilm(filmsStorage.getFilmById(filmId));
        isExsistUser(userStorage.getUserById(userId));
        likeStorage.deleteLike(filmId, userId);

        eventStorage.addEvent(Event.builder()
                .timestamp(Instant.now())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(EventOperation.REMOVE)
                .entityId(filmId)
                .build());
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        log.info("getPopular: count-{} genreId-{} year-{}", count, genreId, year);
        List<Film> films = filmsStorage.getPopular(count, genreId, year);
        return fillMovieData(films);
    }


    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        validationIsExsist(directorStorage.getById(directorId));
        List<Film> directorFilms = fillMovieData(filmsStorage.getFilmsByDirectorId(directorId));

        if (sortBy.equals("likes")) {
            return directorFilms
                    .stream()
                    .sorted(Comparator.comparing(film -> ((Film) film).getLikes().size()).reversed())
                    .toList();
        }
        return directorFilms.stream()
                .sorted(Comparator.comparing(Film::getReleaseDate))
                .toList();
    }

    private void assignDirectorsToFilm(List<Director> directors, Integer id) {
        if (directors != null && !directors.isEmpty()) {
            directors.forEach(director -> directorStorage.assignDirector(id, director.getId()));
        }
    }

    private void assignGenresToFilm(Set<Genre> genres, int filmId) {
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> genreStorage.assignGenre(filmId, genre.getId()));
        }
    }

    private Film setFilmData(Film film) {
        checkCorrectFilm(film);
        Set<Genre> genres = genreStorage.getByFilmId(film.getId());
        film.setGenres(genres);
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
        Map<Integer, Set<Genre>> genres = genreStorage.getAllGenresByFilmId();
        Map<Integer, MPA> mpa = mpaStorage.getAll();
        Map<Integer, List<Director>> directors = directorStorage.getAllDirectorsByFilmId();
        List<Film> list = new ArrayList<>();
        for (Film film : filmList) {
            Integer filmId = film.getId();
            film.setLikes(likes.get(filmId));
            if (film.getLikes() == null) film.setLikes(new ArrayList<>());
            film.setGenres(genres.get(filmId));
            if (film.getGenres() == null) film.setGenres(new HashSet<>());
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

    public List<Film> searchFilms(String query, String by) {
        if (query == null || query.trim().isEmpty()) {
            throw new BadRequestException("Параметр query не может быть пустым");
        }

        if (by == null || by.trim().isEmpty()) {
            throw new BadRequestException("Параметр by не может быть пустым");
        }

        String[] searchFields = by.split(",");
        boolean searchByTitle = false;
        boolean searchByDirector = false;

        for (String field : searchFields) {
            if (field.equalsIgnoreCase("title")) {
                searchByTitle = true;
            } else if (field.equalsIgnoreCase("director")) {
                searchByDirector = true;
            } else {
                throw new BadRequestException("Неизвестный параметр поиска " + field);
            }
        }

        List<Film> films = filmsStorage.searchFilms(query, searchByTitle, searchByDirector);
        films.forEach(this::setFilmData);
        films.sort(Comparator.comparing((Film film) -> film.getLikes().size()).reversed());

        return films;
    }

    public void deleteFilm(Integer filmId) {
        Optional<Film> optionalFilm = filmsStorage.getFilmById(filmId);
        if (optionalFilm.isPresent()) {
            filmsStorage.deleteFilmById(filmId);
        }
    }
}

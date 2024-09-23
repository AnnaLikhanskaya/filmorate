//package ru.yandex.practicum.filmorate.dao.film;
//
//import lombok.extern.slf4j.Slf4j;
//import ru.yandex.practicum.filmorate.exception.NoExceptionObject;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.dao.FilmStorage;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//public class InMemoryFilmStorage implements FilmStorage {
//    private final Map<Integer, Film> films = new HashMap<>();
//    private Integer id = 1;
//
//    private int createId() {
//        return id++;
//    }
//
//
//    @Override
//    public List<Film> getFilms() {
//        return new ArrayList<>(films.values());
//    }
//
//    @Override
//    public Film addFilm(Film film) {
//        film.setId(createId());
//        films.put(film.getId(), film);
//        return film;
//    }
//
//    @Override
//    public Film updateFilms(Film film) {
//        if (films.containsKey(film.getId())) {
//            film.setLikes(films.get(film.getId()).getLikes());
//            films.put(film.getId(), film);
//            return film;
//        } else {
//            throw new NoExceptionObject("Фильм не существует");
//        }
//    }
//
//    @Override
//    public Film getFilmById(Integer id) {
//        if (films.containsKey(id)) {
//            return films.get(id);
//        } else {
//            throw new NoExceptionObject("Фильм не найден");
//        }
//    }
//    @Override
//    public void addLike(Integer filmId, Integer userId) {
//
//    }
//
//    @Override
//    public void deleteLike(Integer filmId, Integer userId) {
//
//    }
//
//    @Override
//    public List<Film> getPopularFilms(int count) {
//        return null;
//    }
//}

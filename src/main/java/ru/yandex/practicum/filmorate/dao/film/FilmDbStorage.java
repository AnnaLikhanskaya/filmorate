package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return super.findMany(sql);
    }

    @Override
    public Film addFilm(Film film) {
        String query = "INSERT INTO films (name, " +
                "releasedate, mpa_id, duration, description)" +
                "VALUES (?, ?, ?, ?, ?)";
        Integer mpaId;
        if (film.getMpa() == null) {
            mpaId = null;
        } else {
            mpaId = film.getMpa().getId();
        }
        int newId = super.insert(
                query,
                film.getName(),
                film.getReleaseDate(),
                mpaId,
                film.getDuration(),
                film.getDescription()
        );
        film.setId(newId);
        return film;
    }


    @Override
    public Film updateFilms(Film film) {
        String query = "UPDATE FILMS SET " +
                "NAME = ?," +
                "RELEASEDATE = ?, " +
                "MPA_ID = ?, " +
                "DURATION = ?, " +
                "DESCRIPTION = ? " +
                "WHERE ID = ?";
        Integer mpaId;
        if (film.getMpa() == null) {
            mpaId = null;
        } else {
            mpaId = film.getMpa().getId();
        }
        super.update(query,
                film.getName(),
                film.getReleaseDate(),
                mpaId,
                film.getDuration(),
                film.getDescription(),
                film.getId());
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Integer filmId) {
        String query = "SELECT * FROM films " +
                "WHERE ID = ?";
        return super.findOne(query, filmId);
    }

    @Override
    public Set<Film> getFilmsByUserId(Integer userId) {
        String query = "SELECT * FROM films JOIN film_likes ON films.id = film_likes.film_id WHERE film_likes.user_id = ?";
        return new HashSet<>(findMany(query, userId));
    }


    public List<Film> getPopular(Integer count, Integer genreId, Integer year) {
        StringBuilder query = new StringBuilder(
                "SELECT f.* " +
                        "FROM FILMS f " +
                        "LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID " +
                        "LEFT JOIN FILM_LIKES fl ON f.ID = fl.FILM_ID "
        );
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (genreId != null) {
            conditions.add("fg.GENRE_ID = ?");
            params.add(genreId);
        }
        if (year != null) {
            conditions.add("YEAR(f.RELEASEDATE) = ?");
            params.add(year);
        }
        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        query.append(" GROUP BY f.ID ORDER BY COUNT(fl.film_id) DESC LIMIT ?;");
        params.add(count);

        return findMany(query.toString(), params.toArray());
    }
}


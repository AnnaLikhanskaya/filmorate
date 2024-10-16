package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector) {
        if (!searchByTitle && !searchByDirector) {
            throw new IllegalArgumentException("Нужно указать хотя бы одно поле для поиска: title или director");
        }

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT f.* FROM FILMS f " +
                        "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.film_id " +
                        "LEFT JOIN DIRECTOR d ON fd.director_id = d.id WHERE ");

        if (searchByTitle && searchByDirector) {
            sql.append("(LOWER(f.name) LIKE LOWER(?) OR LOWER(d.name) LIKE LOWER(?))");
            return jdbc.query(sql.toString(), mapper, "%" + query + "%", "%" + query + "%");
        } else if (searchByTitle) {
            sql.append("LOWER(f.name) LIKE LOWER(?)");
            return jdbc.query(sql.toString(), mapper, "%" + query + "%");
        } else {
            sql.append("LOWER(d.name) LIKE LOWER(?)");
            return jdbc.query(sql.toString(), mapper, "%" + query + "%");
        }
    }

    @Override
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

    @Override
    public void deleteFilmById(Integer filmId) {
        super.delete("DELETE FROM films where id = ?", filmId);
    }
}
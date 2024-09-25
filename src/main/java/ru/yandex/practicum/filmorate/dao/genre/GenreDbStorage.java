package ru.yandex.practicum.filmorate.dao.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }


    @Override
    public Map<Integer, List<Genre>> getAllGenresByFilmId() {
        String genresSqlQuery = "SELECT * FROM GENRES";
        Map<Integer, Genre> genres = super
                .findMany(genresSqlQuery).stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre));
        HashMap<Integer, List<Genre>> genresByFilmsId = new HashMap<>();
        String filmGenresQuery = "SELECT * FROM FILM_GENRE";
        SqlRowSet rowSet = jdbc.queryForRowSet(filmGenresQuery);
        while (rowSet.next()) {
            int film_id = rowSet.getInt("film_id");
            int genre_id = rowSet.getInt("genre_id");
            if (!genresByFilmsId.containsKey(film_id)) {
                genresByFilmsId.put(film_id, new ArrayList<>());
            }
            genresByFilmsId.get(film_id).add(genres.get(genre_id));
        }
        return genresByFilmsId;
    }

    @Override
    public Optional<Genre> getById(Integer id) {
        String sqlQuery = "SELECT * FROM GENRES WHERE ID = ?";
        return super.findOne(sqlQuery, id);
    }

    @Override
    public List<Genre> getByFilmId(Integer filmId) {
        String sqlQuery = "SELECT GENRES.ID,GENRES.NAME " +
                "FROM FILM_GENRE LEFT JOIN GENRES ON GENRES.ID=FILM_GENRE.GENRE_ID  WHERE FILM_ID = ?";
        return super.findMany(sqlQuery, filmId);
    }

    @Override
    public void assignGenre(Integer filmId, Integer genreId) {
        String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) " +
                "VALUES(?, ?)";
        super.insert(sqlQuery, filmId, genreId);
    }

    @Override
    public void deleteGenresByFilmId(Integer filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? ";
        super.delete(sqlQuery, filmId);
    }
}
package ru.yandex.practicum.filmorate.dao.director;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper);

    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT * FROM DIRECTOR";
        return super.findMany(sql);
    }

    @Override
    public Director addDirector(Director director) {
        director.setId(super.insert(
                "INSERT INTO director (name)" +
                        "VALUES (?)",
                director.getName()));
        return director;
    }


    @Override
    public Optional<Director> getById(Integer id) {
        String sqlQuery = "SELECT * FROM DIRECTOR WHERE ID = ?";
        return super.findOne(sqlQuery, id);
    }

    @Override
    public Director updateDirector(Director director) {
        super.update("UPDATE DIRECTOR SET " +
                        "NAME = ?",
                director.getName());
        return director;
    }

    @Override
    public void deleteDirector(Integer id) {
        super.delete("DELETE FROM DIRECTOR where ID = ?",
                id);
    }

    @Override
    public Map<Integer, List<Director>> getAllDirectorsByFilmId() {
        String directorSqlQuery = "SELECT * FROM DIRECTOR";
        Map<Integer, Director> directors = super
                .findMany(directorSqlQuery).stream()
                .collect(Collectors.toMap(Director::getId, director -> director));
        HashMap<Integer, List<Director>> directorsByFilmsId = new HashMap<>();
        String filmDirectorsQuery = "SELECT * FROM FILM_DIRECTOR";
        SqlRowSet rowSet = jdbc.queryForRowSet(filmDirectorsQuery);
        while (rowSet.next()) {
            int filmId = rowSet.getInt("film_id");
            int directorId = rowSet.getInt("director_id");
            if (!directorsByFilmsId.containsKey(filmId)) {
                directorsByFilmsId.put(filmId, new ArrayList<>());
            }
            directorsByFilmsId.get(filmId).add(directors.get(directorId));
        }
        return directorsByFilmsId;
    }

    @Override
    public List<Director> getByFilmId(Integer id) {
        String sqlQuery = "SELECT DIRECTOR.ID,DIRECTOR.NAME " +
                "FROM FILM_DIRECTOR LEFT JOIN DIRECTOR ON DIRECTOR.ID=FILM_DIRECTOR.DIRECTOR_ID  WHERE FILM_ID = ?";
        return super.findMany(sqlQuery, id);
    }

    @Override
    public void deleteDirectorsByFilmId(Integer id) {
        String sqlQuery = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ? ";
        super.delete(sqlQuery, id);
    }

    @Override
    public void assignDirector(Integer filmId, Integer directorId) {
        String sqlQuery = "INSERT INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) " +
                "VALUES(?, ?)";
        super.insert(sqlQuery, filmId, directorId);
    }
}

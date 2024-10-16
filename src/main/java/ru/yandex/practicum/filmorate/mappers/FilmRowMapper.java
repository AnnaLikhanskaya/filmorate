package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class FilmRowMapper implements RowMapper<Film> {
    protected final JdbcTemplate jdbc;

    public FilmRowMapper(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("ID"));
        film.setName(rs.getString("NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate(rs.getDate("RELEASEDATE").toLocalDate());
        film.setDuration(rs.getLong("DURATION"));
        MPA mpa = new MPA();
        mpa.setId(rs.getInt("MPA_ID"));
        film.setMpa(mpa);
        return film;
    }
}
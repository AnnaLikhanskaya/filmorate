package ru.yandex.practicum.filmorate.dao.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.mappers.MPARowMapper;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MPADbStorage extends BaseRepository<MPA> implements MpaStorage {

    @Autowired
    public MPADbStorage(JdbcTemplate jdbc, MPARowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Map<Integer, MPA> getAll() {
        String mpaQuery = "SELECT * FROM MPA";
        Map<Integer, MPA> mpaMap = super.findMany(mpaQuery).stream()
                .collect(Collectors.toMap(MPA::getId, mpa -> mpa));
        return mpaMap;
    }

    @Override
    public Optional<MPA> getById(Integer id) {
        String sqlQuery = "SELECT * FROM MPA WHERE ID = ?";
        return super.findOne(sqlQuery, id);
    }
}
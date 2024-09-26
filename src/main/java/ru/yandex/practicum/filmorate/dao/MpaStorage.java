package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Map;
import java.util.Optional;

public interface MpaStorage {

    Map<Integer, MPA> getAll();

    Optional<MPA> getById(Integer id);

}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class MpaService {
    @Autowired
    private MpaStorage mpaStorage;

    public Map<Integer, MPA> getAll() {
        log.info("Получен запрос на получение списка MPA-рейтинга");
        return mpaStorage.getAll();
    }

    public MPA getById(Integer id) {
        log.info("Получен запрос на получение MPA-рейтинга по ID");
        Optional<MPA> mpa = mpaStorage.getById(id);
        if (mpa.isEmpty()) {
            throw new NotFoundException("Рейтинг MPA с ID: " + id + "не найден");
        }
        return mpa.get();
    }

}
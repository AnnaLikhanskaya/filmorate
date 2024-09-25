package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(@Qualifier("genreDbStorage") GenreStorage storage) {
        this.genreStorage = storage;
    }

    public List<Genre> getAll() {
        log.info("Получен запрос на список жанров");
        return genreStorage.getAll();
    }

    public Optional<Genre> getById(Integer id) {
        log.info("Получен запрос на получение жанра по ID");
        return genreStorage.getById(id);
    }
}
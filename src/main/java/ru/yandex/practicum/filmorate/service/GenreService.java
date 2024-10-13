package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.validation.GenreValidation.isFoundGenre;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        log.info("Получен запрос на список жанров");
        return genreStorage.getAll();
    }

    public Optional<Genre> getById(Integer id) {
        log.info("Получен запрос на получение жанра по ID");
        Optional<Genre> genre = genreStorage.getById(id);
        isFoundGenre(genre);
        return genre;
    }
}
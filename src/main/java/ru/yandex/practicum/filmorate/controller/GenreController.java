package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/genres")
public class GenreController {
    @Autowired
    private GenreService service;

    @GetMapping
    public List<Genre> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Integer id) {
        Optional<Genre> genre = service.getById(id);
        if (genre.isEmpty()) {
            throw new NotFoundException("Жанр не найден");
        }
        return genre.get();
    }
}
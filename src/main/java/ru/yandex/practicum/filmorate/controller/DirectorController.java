package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;


@RestController
@Slf4j
@Validated
@RequestMapping("/directors")
public class DirectorController {
    @Autowired
    private DirectorService directorService;

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Получен запрос на список режиссёров");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable Integer id) {
        log.info("Получен запрос на режиссёра с id: " + id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director postDirector(@Valid @RequestBody Director director) {
        Director added = directorService.addDirector(director);
        log.info("Режиссер добавлен " + director.getId() + ". Имя режиссёра + " + director.getName());
        return added;
    }


    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Режиссёр" + director.getName() + "обновлён");
        return directorService.updateDirector(director);
    }


    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Integer id) {
        log.info("У фильма {} удален режиссёр", id);
        directorService.deleteDirector(id);
    }


}

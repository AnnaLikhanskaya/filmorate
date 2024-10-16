package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.validation.DirectorValidation.validationDirector;
import static ru.yandex.practicum.filmorate.validation.DirectorValidation.validationIsExsist;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;


    public List<Director> getAllDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(Integer id) {
        Optional<Director> director = directorStorage.getById(id);
        validationIsExsist(director);
        return director.get();
    }

    public Director addDirector(Director director) {
        validationDirector(director);
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        validationIsExsist(directorStorage.getById(director.getId()));
        validationDirector(director);
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(Integer id) {
        validationIsExsist(directorStorage.getById(id));
        directorStorage.deleteDirector(id);
    }

}
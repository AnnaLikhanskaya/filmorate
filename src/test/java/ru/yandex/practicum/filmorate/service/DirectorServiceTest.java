package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class DirectorServiceTest {
    @Autowired
    private DirectorService directorService;

    @Autowired
    private DirectorStorage directorStorage;
    private Director director1;
    private Director director2;

    @BeforeEach
    void setUp() {
        director1 = new Director(1, "Режиссёр 1");
        director2 = new Director(2, "Режиссёр 2");
        directorStorage.addDirector(director1);
        directorStorage.addDirector(director2);
    }

    @Test
    void testAddDirector() {
        Director newDirector = new Director(3, "Новый Режиссёр");
        Director addedDirector = directorService.addDirector(newDirector);
        assertNotNull(addedDirector);
        assertEquals("Новый Режиссёр", addedDirector.getName());
    }

    @Test
    void testGetAllDirectors() {
        List<Director> directors = directorService.getAllDirectors();
        assertNotNull(directors);
        assertEquals(2, directors.size());
    }

    @Test
    void testGetDirectorById() {
        Director director = directorService.getDirectorById(1);
        assertNotNull(director);
        assertEquals("Режиссёр 1", director.getName());
    }

    @Test
    void testGetDirectorByIdNotFound() {
        assertThrows(RuntimeException.class, () -> {
            directorService.getDirectorById(999);
        });
    }

    @Test
    void testUpdateDirector() {
        Director addedDirector = directorService.getAllDirectors().get(0);
        Director updatedDirector = new Director(addedDirector.getId(), "Обновлённый Режиссёр");
        Director result = directorService.updateDirector(updatedDirector);
        assertNotNull(result);
        assertEquals("Обновлённый Режиссёр", result.getName());
    }

    @Test
    void testDeleteDirector() {
        Director addedDirector = directorService.getAllDirectors().get(0);
        directorService.deleteDirector(addedDirector.getId());
        assertThrows(RuntimeException.class, () -> {
            directorService.getDirectorById(1);
        });
    }

    @Test
    void testDeleteDirectorNotFound() {
        assertThrows(RuntimeException.class, () -> {
            directorService.deleteDirector(999);
        });
    }


}

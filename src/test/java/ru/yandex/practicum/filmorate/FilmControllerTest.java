package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static java.net.http.HttpClient.newHttpClient;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    private ConfigurableApplicationContext context;
    private final URI url = URI.create("http://localhost:8080/films");
    private Film nullname;
    private Film incorrectDescription;
    private Film incorrectReleaseDate;
    private Film negativeDuration;
    private Film correctFilm;
    private Film nonexistentId;

//    @BeforeEach
//    public void init() {
//        context = SpringApplication.run(FilmorateApplication.class);
//        nullname = new Film(null, null, null,
//                "Описание", LocalDate.of(1946, 8, 20), 100);
//        incorrectDescription = new Film(null, null,
//                "Название", "ОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписание" +
//                "ОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОписаниеОпис" +
//                "аниеОписаниеОписаниеОписаниеОписание", LocalDate.of(1946, 8, 20), 100);
//        incorrectReleaseDate = new Film(null, null,
//                "Название", "Описание",
//                LocalDate.of(1884, 8, 20), 100);
//        negativeDuration = new Film(null, null,
//                "Название", "Описание",
//                LocalDate.of(1946, 8, 20), -3);
//        correctFilm = new Film(1, null,
//                "Название", "Описание",
//                LocalDate.of(1946, 8, 20), 100);
//        nonexistentId = new Film(9999, null,
//                "Название", "Описание",
//                LocalDate.of(1946, 8, 20), 100);
//    }

    private int postToServer(Film film) throws IOException, InterruptedException {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        Gson gson = gsonBuilder.create();

        String userSerialized = gson.toJson(film);

        HttpClient client = newHttpClient();

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(userSerialized);

        HttpRequest requestNullLogin = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(body).build();
        HttpResponse<String> responseNullLogin = client.send(requestNullLogin, HttpResponse.BodyHandlers.ofString());
        return responseNullLogin.statusCode();
    }

    private int putToServer(Film film) throws IOException, InterruptedException {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        Gson gson = gsonBuilder.create();
        String userSerialized = gson.toJson(film);

        HttpClient client = newHttpClient();

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(userSerialized);

        HttpRequest requestNullLogin = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .PUT(body).build();
        HttpResponse<String> responseNullLogin = client.send(requestNullLogin, HttpResponse.BodyHandlers.ofString());
        return responseNullLogin.statusCode();
    }


    @Test
    void postFilms() {
        assertAll(
                () -> assertEquals(400, postToServer(nullname),
                        "Имя не должно быть null, код 400"),
                () -> assertEquals(400, postToServer(incorrectDescription),
                        "Описание не должно привышать 200 символов, код 400"),
                () -> assertEquals(400, postToServer(incorrectReleaseDate),
                        "релиз не может быть раньше 28.12.1895, код 400"),
                () -> assertEquals(400, postToServer(negativeDuration),
                        "продолжительность не может быть отрицательной, код 400"),
                () -> assertEquals(200, postToServer(correctFilm),
                        "Фильм создан верно, код 200")
        );
    }

    @Test
    void putFilms() throws IOException, InterruptedException {
        postToServer(correctFilm);
        assertAll(
                () -> assertEquals(400, putToServer(nullname),
                        "Имя не должно быть null, код 400"),
                () -> assertEquals(400, putToServer(incorrectDescription),
                        "Описание не должно привышать 200 символов, код 400"),
                () -> assertEquals(400, putToServer(incorrectReleaseDate),
                        "релиз не может быть раньше 28.12.1895, код 400"),
                () -> assertEquals(400, putToServer(negativeDuration),
                        "продолжительность не может быть отрицательной, код 400"),
                () -> assertEquals(404, putToServer(nonexistentId),
                        "Фильм не существует, код 404"),
                () -> assertEquals(200, putToServer(correctFilm),
                        "Фильм создан верно, код 200")
        );
    }

    @AfterEach
    public void close() {
        SpringApplication.exit(context);
    }
}
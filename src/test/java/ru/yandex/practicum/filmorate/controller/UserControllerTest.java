package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import ru.yandex.practicum.filmorate.FilmorateApplication;

import org.springframework.test.context.ActiveProfiles;

import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {
    private final URI url = URI.create("http://localhost:8080/users");
    private User nullLogin;
    private User nullEmail;
    private User incorrectLogin;
    private User incorrectBirthday;
    private User incorrectEmail;
    private User nullName;
    private User nonexistentId;

    @BeforeEach
    public void init() {
        nullLogin = new User(null, "mail@mail.ru", null, "Имя",
                LocalDate.of(1946, 8, 20), null);
        nullEmail = new User(null, null, null, "Имя",
                LocalDate.of(1946, 8, 20), null);
        incorrectLogin = new User(1, "mail@mail.ru", "Ло гин", "Имя",
                LocalDate.of(1946, 8, 20), null);
        incorrectBirthday = new User(null, "mail@mail.ru", "Логин", "Имя",
                LocalDate.of(2050, 8, 20), null);
        incorrectEmail = new User(null, "mailmail.ru",
                "Логин", "Имя", LocalDate.of(1946, 8, 20), null);
        nullName = new User(1, "mail@mail.ru",
                "Логин", "Имя", LocalDate.of(1946, 8, 20), null);
        nonexistentId = new User(9999, "mail@mail.ru",
                "Логин", "Имя", LocalDate.of(1946, 8, 20), null);
    }

    private int postToServer(User user) throws IOException, InterruptedException {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        Gson gson = gsonBuilder.create();

        String userSerialized = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(userSerialized);
        HttpRequest requestNullLogin = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(body).build();
        HttpResponse<String> responseNullLogin = client.send(requestNullLogin, HttpResponse.BodyHandlers.ofString());
        return responseNullLogin.statusCode();
    }

    private int putToServer(User user) throws IOException, InterruptedException {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        Gson gson = gsonBuilder.create();
        String userSerialized = gson.toJson(user);

        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(userSerialized);
        HttpRequest requestNullLogin = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .PUT(body).build();
        HttpResponse<String> responseNullLogin = client.send(requestNullLogin, HttpResponse.BodyHandlers.ofString());
        return responseNullLogin.statusCode();
    }

    @Test
    void postUsers() {
        assertAll(
                () -> assertEquals(400, postToServer(nullLogin),
                        "Логин не должен быть null, статус 400"),
                () -> assertEquals(400, postToServer(incorrectLogin),
                        "Логин не должен содержать пробелы, статус 400"),
                () -> assertEquals(400, postToServer(incorrectEmail),
                        "Email некоррекный, статус 400"),
                () -> assertEquals(400, postToServer(nullEmail),
                        "Email не должен быть null, статус 400"),
                () -> assertEquals(400, postToServer(incorrectBirthday),
                        "День рождения не должен быть в будующем, статус 400"),
                () -> assertEquals(500, postToServer(nullName),
                        "Имя может быть пустым, статус 200")
        );
    }

    @Test
    void putUsers() throws IOException, InterruptedException {
        postToServer(nullName);
        assertAll(
                () -> assertEquals(400, putToServer(nullLogin),
                        "Логин не должен быть null, статус 400"),
                () -> assertEquals(400, putToServer(incorrectLogin),
                        "Логин не должен содержать пробелы, статус 400"),
                () -> assertEquals(400, putToServer(incorrectEmail),
                        "Email некоррекный, статус 400"),
                () -> assertEquals(400, putToServer(nullEmail),
                        "Email не должен быть null, статус 400"),
                () -> assertEquals(400, putToServer(incorrectBirthday),
                        "День рождения не должен быть в будующем, статус 400"),
                () -> assertEquals(200, putToServer(nullName),
                        "Имя может быть пустым, статус 200"),
                () -> assertEquals(404, putToServer(nonexistentId),
                        "Такого пользователя не существует, статус 404")
        );
    }

}
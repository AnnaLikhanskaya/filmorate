package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FilmControllerTest {
//
//    private final URI url = URI.create("http://localhost:8080/films");
//    private Film nullname;
//    private Film incorrectDescription;
//    private Film incorrectReleaseDate;
//    private Film negativeDuration;
//    private Film correctFilm;
//    private Film nonexistentId;
//
//    @BeforeEach
//    public void init() {
//        nullname = new Film(null, null, null, "Duis in consequat esse",
//                LocalDate.of(1946, 8, 20), 100L, null, null, null);
//        incorrectDescription = new Film(null, null,
//                "labore nulla", "Пятеро друзей ( комик-группа «Шарло»)," +
//                " приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова," +
//                " который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия»," +
//                " стал кандидатом Коломбани.",
//                LocalDate.of(1946, 8, 20), 100L, null, null, null);
//        incorrectReleaseDate = new Film(null, null, "labore nulla", "Duis in consequat esse",
//                LocalDate.of(1884, 8, 20), 100L, null, null, null);
//        negativeDuration = new Film(null, null, "labore nulla", "Duis in consequat esse",
//                LocalDate.of(1946, 8, 20), -3L, null, null, null);
//        correctFilm = new Film(1, null, "labore nulla", "Duis in consequat esse",
//                LocalDate.of(1946, 8, 20), 100L, null, null, null);
//        nonexistentId = new Film(9999, null, "labore nulla", "Duis in consequat esse",
//                LocalDate.of(1946, 8, 20), -3L, null, null, null);
//    }
//
//    private int postToServer(Film film) throws IOException, InterruptedException {
//        GsonBuilder gsonBuilder = new GsonBuilder()
//                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
//        Gson gson = gsonBuilder.create();
//        String userSerialized = gson.toJson(film);
//        HttpClient client = HttpClient.newHttpClient();
//        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(userSerialized);
//        HttpRequest requestNullLogin = HttpRequest.newBuilder()
//                .uri(url)
//                .header("Content-Type", "application/json")
//                .POST(body).build();
//        HttpResponse<String> responseNullLogin = client.send(requestNullLogin, HttpResponse.BodyHandlers.ofString());
//        return responseNullLogin.statusCode();
//    }
//
//
//    private int putToServer(Film film) throws IOException, InterruptedException {
//        GsonBuilder gsonBuilder = new GsonBuilder()
//                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
//        Gson gson = gsonBuilder.create();
//        String userSerialized = gson.toJson(film);
//        HttpClient client = HttpClient.newHttpClient();
//        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(userSerialized);
//
//        HttpRequest requestNullLogin = HttpRequest.newBuilder()
//                .uri(url)
//                .header("Content-Type", "application/json")
//                .PUT(body).build();
//        HttpResponse<String> responseNullLogin = client.send(requestNullLogin, HttpResponse.BodyHandlers.ofString());
//        return responseNullLogin.statusCode();
//    }
//
//    @Test
//    void postFilms() {
//        assertAll(
//                () -> assertEquals(400, postToServer(nullname),
//                        "Имя не должно быть null, статус 400"),
//                () -> assertEquals(400, postToServer(incorrectDescription),
//                        "Описание не должно привышать 200 символов, статус 400"),
//                () -> assertEquals(400, postToServer(incorrectReleaseDate),
//                        "релиз не может быть раньше 28.12.1895, статус 400"),
//                () -> assertEquals(400, postToServer(negativeDuration),
//                        "продолжительность не может быть отрицательной, статус 400")
//        );
//    }
//
//    @Test
//    void putUsers() throws IOException, InterruptedException {
//        postToServer(correctFilm);
//        assertAll(
//                () -> assertEquals(400, putToServer(nullname),
//                        "Имя не должно быть null, статус 400"),
//                () -> assertEquals(400, putToServer(incorrectDescription),
//                        "Описание не должно привышать 200 символов, статус 400"),
//                () -> assertEquals(400, putToServer(incorrectReleaseDate),
//                        "релиз не может быть раньше 28.12.1895, статус 400"),
//                () -> assertEquals(400, putToServer(negativeDuration),
//                        "продолжительность не может быть отрицательной, статус 400"),
//                () -> assertEquals(400, putToServer(nonexistentId),
//                        "Фильм не существует, статус 404"),
//                () -> assertEquals(400, putToServer(correctFilm),
//                        "Фильм создан верно, статус 200")
//        );
//    }
}

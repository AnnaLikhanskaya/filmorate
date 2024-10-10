package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        log.info("addReview...");
        if (review == null){
            // В метод передали пустой объект
            log.error("addReview: object review is null");
            throw new NullPointerException("review is null");
        }
        /* todo Может ли пользователь на один фильм давать несколько отзывов?
        *  На обсуждении у команды
        */

        checkUserAndFilm(review);
        // Инициализируем рейтинг полезности
        review.setUseful(0);
        return reviewStorage.addReview(review);
    }

    private void checkUserAndFilm(Review review) {
        int filmId = review.getFilmId();
        int userId = review.getUserId();
        // Проверка ссылки на Пользователя
        if (userStorage.getUserById(userId).isEmpty()) {
            String message = "User with id " + userId + " not found.";
            log.error(message);
            throw new NotFoundException(message);
        }
        // Проверка ссылки на Фильм
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            String message = "Film with id " + filmId + " not found.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    public Review updateReview(Review review) {
        chekExistsReview(review);
        checkUserAndFilm(review);
        return reviewStorage.updateReview(review);
    }

    private void chekExistsReview(Review review) {
        Optional<Review> reviewOptional = reviewStorage.getById(review.getReviewId());
        if (reviewOptional.isPresent()) {
            return;
        }
        throw new NotFoundException("review not found");
    }

}

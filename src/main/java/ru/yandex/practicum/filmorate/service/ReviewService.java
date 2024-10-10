package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
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
        if (review == null) {
            throw new BadRequestException("Invalid review data");
        }
        int filmId = review.getFilmId();
        int userId = review.getUserId();

        if (userStorage.getUserById(userId).isEmpty()) {
            String message = "User with id " + userId + " not found.";
            log.error(message);
            throw new NotFoundException(message);
        }

        if (filmStorage.getFilmById(filmId).isEmpty()) {
            String message = "Film with id " + filmId + " not found.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    public Review updateReview(Review review) {
        if (review == null || review.getReviewId() == null) {
            throw new BadRequestException("Invalid review data");
        }
        existsReviewByid(review.getReviewId());
        checkUserAndFilm(review);
        return reviewStorage.updateReview(review);
    }

    private void existsReviewByid(Integer reviewId) {
        if (reviewId == null){
            throw new BadRequestException("reviewId is null");
        }
        boolean existsReview = reviewStorage.existsById(reviewId);
        if (existsReview){
            return;
        }
        throw  new NotFoundException("review not found");
    }

    public Review getById(int reviewId) {
        Optional<Review> reviewOptional = reviewStorage.getById(reviewId);
        if (reviewOptional.isPresent()) {
            return reviewOptional.get();
        }
        throw new NotFoundException("review not found");
    }

    public void deleteReviewByid(int reviewId) {
        log.trace("Delete entity with id={}.", reviewId);
        existsReviewByid(reviewId);

        boolean deleteSuccess = reviewStorage.deleteReviewById(reviewId);
        if (!deleteSuccess){
            throw new NotFoundException("Not delete review");
        }
    }

    public List<Review> getReviewByFilmOrAll(int filmId, int count) {
        if (filmId == 0) {
            return reviewStorage.getTopReviews(count);
        }
        return reviewStorage.getReviewsByFilm(filmId, count);
    }
}

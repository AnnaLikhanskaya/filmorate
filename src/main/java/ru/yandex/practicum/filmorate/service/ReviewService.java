package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    public static final String REVIEW_NOT_FOUND = "Review with id %d not found.";
    private static final String REVIEW_NULL_ERROR = "Review is null";
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    private static void validateReview(Review review) {
        if (review == null) {
            log.error(REVIEW_NULL_ERROR);
            throw new NullPointerException(REVIEW_NULL_ERROR);
        }
    }

    public Review addReview(Review review) {
        log.info("addReview...");
        checkUserAndFilm(review);
        review.setUseful(0);
        review = reviewStorage.addReview(review);
        eventStorage.addEvent(Event.builder()
                .timestamp(Instant.now())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(EventOperation.ADD)
                .entityId(review.getReviewId())
                .build());
        return review;
    }

    private void checkUserAndFilm(Review review) {
        validateReview(review);
        int filmId = review.getFilmId();
        int userId = review.getUserId();

        existsUserById(userId);

        if (filmStorage.getFilmById(filmId).isEmpty()) {
            String message = "Film with id " + filmId + " not found.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    public Review updateReview(Review review) {
        existsReviewById(review.getReviewId());
        checkUserAndFilm(review);
        review = reviewStorage.updateReview(review);
        eventStorage.addEvent(Event.builder()
                .timestamp(Instant.now())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(EventOperation.UPDATE)
                .entityId(review.getReviewId())
                .build());
        return review;
    }

    private void existsReviewById(int reviewId) {
        if (!reviewStorage.existsById(reviewId)) {
            throw new NotFoundException(String.format(REVIEW_NOT_FOUND, reviewId));
        }
    }

    public Review getById(int reviewId) {
        return reviewStorage.getById(reviewId)
                .orElseThrow(() -> new NotFoundException(String.format(REVIEW_NOT_FOUND, reviewId)));
    }

    public void deleteReviewByid(int reviewId) {
        log.trace("Delete entity with id={}.", reviewId);
        Optional<Review> optReview = reviewStorage.getById(reviewId);
        if (optReview.isEmpty()) {
            throw new NotFoundException(String.format(REVIEW_NOT_FOUND, reviewId));
        }
        reviewStorage.deleteReviewById(reviewId);
        eventStorage.addEvent(Event.builder()
                .timestamp(Instant.now())
                .userId(optReview.get().getUserId())
                .eventType(EventType.REVIEW)
                .operation(EventOperation.REMOVE)
                .entityId(optReview.get().getReviewId())
                .build());
    }

    public List<Review> getReviewByFilmOrAll(int filmId, int count) {
        return (filmId == 0) ? reviewStorage.getTopReviews(count)
                : reviewStorage.getReviewsByFilm(filmId, count);
    }

    public void addReviewLikeOrDislike(int reviewId, int userId, boolean isLike) {
        checkReviewAndUser(reviewId, userId);
        reviewStorage.addReviewLikeOrDislike(reviewId, userId, isLike);
    }

    private void checkReviewAndUser(int reviewId, int userId) {
        existsReviewById(reviewId);
        existsUserById(userId);
    }

    private void existsUserById(int userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            String message = "User with id " + userId + " not found.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    public void deleteReviewLikeOrDislike(int reviewId, int userId, boolean isLike) {
        checkReviewAndUser(reviewId, userId);
        boolean deleted = reviewStorage.deleteReviewLikeOrDislike(reviewId, userId, isLike);
        if (!deleted) {
            throw new NotFoundException("User with id " + userId + " didn't like review with id " + reviewId);
        }
    }
}

package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review addReview(Review review);

    Optional<Review> getById(Integer reviewId);

    Review updateReview(Review review);

    boolean existsById(Integer reviewId);

    boolean deleteReviewById(int reviewId);

    List<Review> getReviewsByFilm(int filmId, int count);

    List<Review> getTopReviews(int count);

    int addReviewLikeOrDislike(int reviewId, int userId, boolean isLike);

    boolean deleteReviewLikeOrDislike(int reviewId, int userId, boolean isLike);
}

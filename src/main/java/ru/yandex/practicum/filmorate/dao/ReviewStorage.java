package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

public interface ReviewStorage {

    Review addReview(Review review);

    Optional<Review> getById(Integer reviewId);

    Review updateReview(Review review);
}

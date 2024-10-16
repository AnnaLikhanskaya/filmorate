package ru.yandex.practicum.filmorate.dao.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ru.yandex.practicum.filmorate.FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewDbStorageTest {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Test
    @Transactional
    public void shouldCreateUpdateDeleteReview() {
        Review review = setInputReview();
        Review createdReview = reviewStorage.addReview(review);
        assertEquals(review, createdReview);
        boolean newPositive = !createdReview.getIsPositive();
        createdReview.setIsPositive(newPositive);
        final Review updatedReview = reviewStorage.updateReview(createdReview);
        assertNotNull(updatedReview);
        int reviewId = updatedReview.getReviewId();
        reviewStorage.deleteReviewById(reviewId);
        assertAll(
                () -> assertEquals(updatedReview.getIsPositive(), newPositive),
                () -> assertTrue(reviewStorage.getById(reviewId).isEmpty())
        );
    }

    @Test
    @Transactional
    public void shouldDelLikeAfterLike() {
        Review review = reviewStorage.addReview(setInputReview());
        User user = userStorage.addUser(Fixtures.getUser2());
        reviewStorage.addReviewLikeOrDislike(review.getReviewId(), user.getId(), true);
        assertTrue(reviewStorage.existsReviewLikes(review.getReviewId(), user.getId(), true));
        reviewStorage.deleteReviewLikeOrDislike(review.getReviewId(), user.getId(), true);
        assertFalse(reviewStorage.existsReviewLikes(review.getReviewId(), user.getId(), true));
    }

    private Review setInputReview() {
        User createdUser = userStorage.addUser(Fixtures.getUser1());
        Film film = Fixtures.getFilm1();
        Film createdFilm = filmStorage.addFilm(film);
        return Fixtures.getReview(createdUser.getId(), createdFilm.getId());
    }
}
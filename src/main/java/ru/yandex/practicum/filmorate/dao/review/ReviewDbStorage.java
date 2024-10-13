package ru.yandex.practicum.filmorate.dao.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String INSERT_REVIEW_QUERY = "INSERT INTO REVIEWS (user_id, film_id, useful, is_positive, content) values (?, ?, ?, ?, ?)";
    private static final String SELECT_REVIEW_BY_ID_QUERY = "SELECT id, user_id, film_id, is_positive, useful, content FROM reviews WHERE id=?";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET is_positive=?, content=? WHERE id=?";
    private static final String EXISTS_REVIEW_QUERY = "SELECT EXISTS (SELECT 1 FROM reviews WHERE id=?)";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE id=?";
    private static final String SELECT_REVIEWS_BY_FILM_QUERY = "SELECT id, user_id, film_id, is_positive, useful, content FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String SELECT_TOP_REVIEWS_QUERY = "SELECT id, user_id, film_id, is_positive, useful, content FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String INSERT_LIKE_DISLIKE_QUERY = "INSERT INTO review_likes (user_id, review_id, is_like) VALUES (?, ?, ?)";
    private static final String EXISTS_REVIEW_LIKES_QUERY = "SELECT EXISTS (SELECT 1 FROM REVIEW_LIKES WHERE review_id = ? AND user_id = ? AND is_like = ?)";
    private static final String DELETE_REVIEW_LIKES_QUERY = "DELETE FROM review_likes WHERE user_id=? AND review_id=? AND is_like=?";
    private static final String UPDATE_USEFUL_REVIEW_QUERY = "UPDATE reviews SET useful = useful %s WHERE id=?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review addReview(Review review) {
        checkReviewNotNull(review);
        int newReviewId = super.insert(INSERT_REVIEW_QUERY,
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.isIsPositive(),
                review.getContent());
        review.setReviewId(newReviewId);
        return review;
    }

    @Override
    public Optional<Review> getById(Integer reviewId) {
        checkReviewIdNotNull(reviewId);
        return super.findOne(SELECT_REVIEW_BY_ID_QUERY, reviewId);
    }

    @Override
    public Review updateReview(Review review) {
        checkReviewNotNull(review);
        super.update(UPDATE_REVIEW_QUERY, review.isIsPositive(), review.getContent(), review.getReviewId());
        return getById(review.getReviewId()).orElseThrow(() -> new NotFoundException("Review not found"));
    }

    @Override
    public boolean existsById(Integer reviewId) {
        checkReviewIdNotNull(reviewId);
        return super.exists(EXISTS_REVIEW_QUERY, reviewId);
    }

    @Override
    public boolean deleteReviewById(int reviewId) {
        return super.delete(DELETE_REVIEW_QUERY, reviewId);
    }

    @Override
    public List<Review> getReviewsByFilm(int filmId, int count) {
        return super.findMany(SELECT_REVIEWS_BY_FILM_QUERY, filmId, count);
    }

    @Override
    public List<Review> getTopReviews(int count) {
        return super.findMany(SELECT_TOP_REVIEWS_QUERY, count);
    }

    @Override
    @Transactional
    public int addReviewLikeOrDislike(int reviewId, int userId, boolean isLike) {
        if (existsReviewLikes(reviewId, userId, !isLike)) {
            deleteReviewLikeOrDislike(reviewId, userId, !isLike);
        }
        int inserted = super.insert(INSERT_LIKE_DISLIKE_QUERY, userId, reviewId, isLike);
        if (inserted > 0) {
            updateUsefulReview(reviewId, isLike);
        }
        return inserted;
    }

    @Override
    public boolean existsReviewLikes(int reviewId, int userId, boolean isLike) {
        return super.exists(EXISTS_REVIEW_LIKES_QUERY, reviewId, userId, isLike);
    }

    @Override
    @Transactional
    public boolean deleteReviewLikeOrDislike(int reviewId, int userId, boolean isLike) {
        boolean deleted = super.delete(DELETE_REVIEW_LIKES_QUERY, userId, reviewId, isLike);
        if (deleted) {
            updateUsefulReview(reviewId, !isLike);
        }
        return deleted;
    }

    private void updateUsefulReview(int reviewId, boolean isLike) {
        String plusMinus = isLike ? "+1" : "-1";
        super.update(String.format(UPDATE_USEFUL_REVIEW_QUERY, plusMinus), reviewId);
    }

    private void checkReviewNotNull(Review review) {
        if (review == null) {
            throw new NotFoundException("Review is null");
        }
    }

    private void checkReviewIdNotNull(Integer reviewId) {
        if (reviewId == null) {
            throw new BadRequestException("Review ID is null");
        }
    }
}

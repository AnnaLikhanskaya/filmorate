package ru.yandex.practicum.filmorate.dao.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.BaseRepository;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review addReview(Review review) {
        if (review == null) {
            throw new NotFoundException("review is null");
        }

        int newReviewId = super.insert(
                "INSERT INTO REVIEWS (user_id, film_id, useful, is_positive, content)" +
                        " values (?, ?, ?, ?, ?)",
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
        if (reviewId == null) {
            throw new BadRequestException("reviewId is null");
        }

        String queryById = "SELECT id, user_id, film_id, is_positive, " +
                "useful, content FROM reviews WHERE id=?";
        return super.findOne(queryById, reviewId);
    }

    @Override
    public Review updateReview(Review review) {
        int reviewId = review.getReviewId();
        String queryUpdReview = "UPDATE reviews SET is_positive=?, content=? "
                + "WHERE id=?";
        super.update(queryUpdReview,
                review.isIsPositive(),
                review.getContent(),
                reviewId
        );
        Optional<Review> reviewOptional = getById(reviewId);
        if (reviewOptional.isPresent()){
            review = reviewOptional.get();
        }
        return review;
    }

    @Override
    public boolean existsById(Integer reviewId) {
        if (reviewId == null ){
            throw new BadRequestException("reviewId is null");
        }
        String query = "SELECT (EXISTS (SELECT 1 FROM reviews WHERE id=?))";
        return super.existsById(query, reviewId);
    }

    @Override
    public boolean deleteReviewById(int reviewId) {
        String query = "DELETE FROM reviews WHERE id=?";
        return super.delete(query, reviewId);
    }
}

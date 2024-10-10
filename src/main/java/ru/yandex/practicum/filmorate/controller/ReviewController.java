package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("Получен запрос на добавление отзыва о фильме");
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Получен запрос на обновление отзыва о фильме");
        return reviewService.updateReview(review);
    }

    @GetMapping("/{reviewId}")
    public Review getById(@PathVariable int reviewId) {
        log.info("Получен запрос на получение отзыва по id: {}", reviewId);
        return reviewService.getById(reviewId);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteById(@PathVariable int reviewId) {
        log.info("Получен запрос на удаление отзыва по id: {}", reviewId);
        reviewService.deleteReviewByid(reviewId);
    }

    @GetMapping
    public List<Review> getByFilmId(@RequestParam(defaultValue = "0") int filmId,
                                    @RequestParam(defaultValue = "10") int count) {
        return null;
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addReviewLike(@PathVariable int reviewId, @PathVariable int userId) {
        ;
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addReviewDislike(@PathVariable int reviewId, @PathVariable int userId) {
        ;
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable int reviewId, @PathVariable int userId) {
        ;
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void deleteReviewLike(@PathVariable int reviewId, @PathVariable int userId) {
        ;
    }
}
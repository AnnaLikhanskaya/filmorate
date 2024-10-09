package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewController {

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return null;
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return null;
    }

    @GetMapping("/{reviewId}")
    public Review getById(@PathVariable int reviewId) {
        return null;
    }

    @DeleteMapping("/{reviewId}")
    public void deleteById(@PathVariable int reviewId) {
        ;
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
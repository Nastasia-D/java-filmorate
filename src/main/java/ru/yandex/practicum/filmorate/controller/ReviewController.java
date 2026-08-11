package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review review) {
        log.info("Запрос на создание отзыва: {}", review);
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Запрос на обновление отзыва с id: {}", review.getReviewId());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление отзыва с id: {}", id);
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        log.info("Запрос на получение отзыва с id: {}", id);
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> findAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        log.info("Запрос на получение отзывов для фильма id: {}, count: {}", filmId, count);
        return reviewService.findAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        log.info("Запрос на добавление лайка от пользователя {} к отзыву {}", userId, id);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id,
                           @PathVariable Long userId) {
        log.info("Запрос на добавление дизлайка от пользователя {} к отзыву {}", userId, id);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        log.info("Запрос на удаление лайка от пользователя {} к отзыву {}", userId, id);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id,
                              @PathVariable Long userId) {
        log.info("Запрос на удаление дизлайка от пользователя {} к отзыву {}", userId, id);
        reviewService.removeDislike(id, userId);
    }
}
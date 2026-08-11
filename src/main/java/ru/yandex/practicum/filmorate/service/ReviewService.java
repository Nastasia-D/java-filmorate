package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final FeedStorage feedStorage;

    public Review create(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        Review createdReview = reviewStorage.create(review);
        feedStorage.addEvent(new Event(null, System.currentTimeMillis(), createdReview.getUserId(), EventType.REVIEW, Operation.ADD, createdReview.getReviewId()));
        return createdReview;
    }

    public Review update(Review review) {
        Review existingReview = getReview(review.getReviewId());
        Review updatedReview = reviewStorage.update(review);
        feedStorage.addEvent(new Event(null, System.currentTimeMillis(), existingReview.getUserId(), EventType.REVIEW, Operation.UPDATE, updatedReview.getReviewId()));
        return updatedReview;
    }

    public void delete(Long reviewId) {
        Review review = getReview(reviewId);
        reviewStorage.delete(reviewId);
        feedStorage.addEvent(new Event(null, System.currentTimeMillis(), review.getUserId(), EventType.REVIEW, Operation.REMOVE, reviewId));
    }

    public Review getReview(Long reviewId) {
        return reviewStorage.findById(reviewId)
                .orElseThrow(() ->
                        new NotFoundException("Отзыв с id = " + reviewId + " не найден"));
    }

    public List<Review> findAll(Long filmId, Integer count) {
        if (filmId != null) {
            filmService.getFilm(filmId);
        }

        return reviewStorage.findAll(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);

        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);

        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);

        reviewStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        getReview(reviewId);
        userService.getUser(userId);

        reviewStorage.removeDislike(reviewId, userId);
    }
}
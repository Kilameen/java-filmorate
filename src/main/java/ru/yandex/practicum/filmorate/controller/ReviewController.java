package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    //Добавление нового отзыва
    @PostMapping
    @Validated(Marker.OnCreate.class)
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review review) {
        log.info("Запрос на создание отзыва {}", review);
        return reviewService.create(review);
    }

    //Редактирование уже имеющегося отзыва
    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Review update(@Valid @RequestBody Review review) {
        log.info("Запрос на обновление отзыва {}", review);
        return reviewService.update(review);
    }

    //Удаление уже имеющегося отзыва
    @DeleteMapping("/{id}")
    @Validated(Marker.OnCreate.class)
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление отзыва с id {}", id);
        reviewService.delete(id);
    }

    //Получение отзыва по идентификатору
    @GetMapping("/{id}")
    @Validated(Marker.OnCreate.class)
    public Review getReview(@PathVariable Long id) {
        log.info("Запрос на получение отзыва с id {}", id);
        return reviewService.getReview(id);
    }

    //Получение всех отзывов по идентификатору фильма
    @GetMapping
    public List<Review> getReviews(
            @RequestParam(defaultValue = "-1") Long filmId,
            @RequestParam(defaultValue = "10") int count) {

        //Если фильм не указан, то все
        if (filmId == null || filmId == -1) {
            log.info("Запрос на получение всех отзывов");
            return reviewService.getAllReviews();
        }
        log.info("Запрос на получение {} отзывов фильма с id {}", count, filmId);
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    //Пользователь ставит лайк отзыву
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        log.info("Пользователь с id {} пытается поставить лайк отзыву с id {}", userId, reviewId);
        reviewService.addLike(reviewId, userId);
    }

    //Пользователь ставит дизлайк отзыву
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        log.info("Пользователь с id {} пытается поставить дизлайк отзыву с id {}", userId, reviewId);
        reviewService.addDislike(reviewId, userId);
    }

    //Пользователь удаляет лайк отзыву
    @DeleteMapping({"/{id}/like/{userId}"})
    public void deleteLike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        log.info("Пользователь с id {} пытается убрать лайк у отзыва с id {}", userId, reviewId);
        reviewService.deleteLike(reviewId, userId);
    }

    //Пользователь удаляет дизлайк отзыву
    @DeleteMapping({"/{id}/dislike/{userId}"})
    public void deleteDislike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        log.info("Пользователь с id {} пытается убрать дизлайк у отзыва с id {}", userId, reviewId);
        reviewService.deleteDislike(reviewId, userId);
    }
}

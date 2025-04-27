package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    //Добавление нового отзыва
    Review create(Review review);

    //Редактирование уже имеющегося отзыва
    Review update(Review review);

    //Удаление уже имеющегося отзыва
    void delete(Long id);

    //Получение отзыва по идентификатору
    Review getReview(Long id);

    //Получение всех отзывов по идентификатору фильма
    //Если фильм не указан, то все
    List<Review> getAllReviews();

    //Если кол-во не указано, то 10
    List<Review> getReviewsByFilmId(Long filmId, int count);

    //Пользователь ставит лайк отзыву
    void addLike(Long reviewId, Long userId);

    //Пользователь ставит дизлайк отзыву
    void addDislike(Long reviewId, Long userId);

    //Пользователь удаляет лайк/дизлайк отзыву
    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}

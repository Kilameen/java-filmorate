package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewDao {

    Review create(Review review);

    Review update(Review review);

    void delete(Long id);

    Review getReviewById(Long id);

    Long getReviewIdByFilmIdAndUserId(Long userId, Long filmId);

    Collection<Review> getReviewsByFilmId(Long id, int count);

    Collection<Review> getAllReviews();

    boolean isReviewExist(Long id);

    void updateUsefulToReview(Long reviewId, int useful);
}

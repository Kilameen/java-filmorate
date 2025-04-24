package ru.yandex.practicum.filmorate.service.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.review.ReviewDao;
import ru.yandex.practicum.filmorate.dao.review.UsefulDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;
    private final UsefulDao usefulDao;

    @Override
    public Review create(Review review) {
        return reviewDao.create(review);
    }

    @Override
    public Review update(Review review) {
        //Если отзыв не найден, то выбросит исключение
        if (!reviewDao.isReviewExist(review.getId())) {
            throw new NotFoundException("Отзыв с id " + review.getId() + " не найден!");
        }
        return reviewDao.update(review);
    }

    @Override
    public void delete(Long id) {
        //Если отзыв не найден, то выбросит исключение
        if (!reviewDao.isReviewExist(id)) {
            throw new NotFoundException("Отзыв с id " + id + " не найден!");
        }
        usefulDao.deleteAllMarks(id);
        log.info("Оценки для фильма с id {} удалены", id);
        reviewDao.delete(id);
        log.info("Фильм с id {} удален", id);
    }

    @Override
    public Review getReview(Long id) {
        return reviewDao.getReviewById(id);
    }

    @Override
    public List<Review> getAllReviews() {
        return (List<Review>) reviewDao.getAllReviews();
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        return (List<Review>) reviewDao.getReviewsByFilmId(filmId,count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        usefulDao.addLike(reviewId, userId);
        int likesCount = usefulDao.getLikesCountForReview(reviewId);
        int dislikesCount = usefulDao.getDislikesCountForReview(reviewId);
        reviewDao.updateUsefulToReview(reviewId, likesCount-dislikesCount);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        usefulDao.addDislike(reviewId, userId);
        int likesCount = usefulDao.getLikesCountForReview(reviewId);
        int dislikesCount = usefulDao.getDislikesCountForReview(reviewId);
        reviewDao.updateUsefulToReview(reviewId, likesCount-dislikesCount);
    }

    @Override
    public void deleteMark(Long reviewId, Long userId) {
        usefulDao.deleteMark(reviewId, userId);
        int likesCount = usefulDao.getLikesCountForReview(reviewId);
        int dislikesCount = usefulDao.getDislikesCountForReview(reviewId);
        reviewDao.updateUsefulToReview(reviewId, likesCount-dislikesCount);
    }
}

package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.review.ReviewDao;
import ru.yandex.practicum.filmorate.dao.review.UsefulDao;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;
    private final UsefulDao usefulDao;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewServiceImpl(@Qualifier("H2ReviewDb") ReviewDao reviewDao, @Qualifier("H2UsefulDb") UsefulDao usefulDao,
                             @Qualifier("H2FilmDb") FilmStorage filmStorage, @Qualifier("H2UserDb") UserStorage userStorage) {
        this.reviewDao = reviewDao;
        this.usefulDao = usefulDao;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Review create(Review review) {
        checkFilmAndUserExist(review.getFilmId(), review.getUserId());
        validateContent(review.getContent());
        try {
            reviewDao.getReviewIdByFilmIdAndUserId(review.getUserId(), review.getFilmId());
            throw new DuplicatedDataException("Отзыв пользователя " + review.getUserId() + " на фильм " + review.getFilmId() + " уже существует");
        } catch (Exception ex) {
            return reviewDao.create(review);
        }
    }

    @Override
    public Review update(Review review) {
        validateContent(review.getContent());
        //Если отзыв не найден, то выбросит исключение
        if (!reviewDao.isReviewExist(review.getReviewId())) {
            try {
                review.setReviewId(reviewDao.getReviewIdByFilmIdAndUserId(review.getUserId(), review.getFilmId()));
            } catch (Exception ex) {
                throw new NotFoundException("Отзыв с id " + review.getReviewId() + " не найден!");
            }
        }
        checkFilmAndUserExist(review.getFilmId(), review.getUserId());
        return reviewDao.update(review);
    }

    @Override
    public void delete(Long id) {
        //Если отзыв не найден, то выбросит исключение
        if (!reviewDao.isReviewExist(id)) {
            throw new NotFoundException("Отзыв с id " + id + " не найден!");
        }
        usefulDao.deleteAllMarks(id);
        reviewDao.delete(id);
        log.info("Отзыв с id {} удален", id);
    }

    @Override
    public Review getReview(Long id) {
        //Если отзыв не найден, то выбросит исключение
        if (!reviewDao.isReviewExist(id)) {
            throw new NotFoundException("Отзыв с id " + id + " не найден!");
        }
        return reviewDao.getReviewById(id);
    }

    @Override
    public List<Review> getAllReviews() {
        return (List<Review>) reviewDao.getAllReviews();
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        filmStorage.getFilm(filmId);
        return (List<Review>) reviewDao.getReviewsByFilmId(filmId, count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        if (!reviewDao.isReviewExist(reviewId)) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден!");
        }
        userStorage.getUserById(userId);
        //проверяем, есть ли дизлайк у такого пользователя и отзыва
        if (usefulDao.isDislikeExist(reviewId, userId)) {
            //если есть, то изменяем дизлайк на лайк
            usefulDao.changeDislikeToLike(reviewId, userId);
        } else {
            //если нет, то ставим лайк
            usefulDao.addLike(reviewId, userId);
        }
        updateUseful(reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        if (!reviewDao.isReviewExist(reviewId)) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден!");
        }
        userStorage.getUserById(userId);
        if (usefulDao.isLikeExist(reviewId, userId)) {
            //если есть, то изменяем дизлайк на лайк
            usefulDao.changeLikeToDislike(reviewId, userId);
        } else {
            //если нет, то ставим лайк
            usefulDao.addDislike(reviewId, userId);
        }
        updateUseful(reviewId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        if (!usefulDao.isLikeExist(reviewId, userId)) {
            throw new NotFoundException("Такой лайк не найден");
        }
        userStorage.getUserById(userId);
        usefulDao.deleteLike(reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        if (!usefulDao.isDislikeExist(reviewId, userId)) {
            throw new NotFoundException("Такой дизлайк не найден");
        }
        userStorage.getUserById(userId);
        usefulDao.deleteDislike(reviewId, userId);
        updateUseful(reviewId);
    }

    private void validateContent(String content) {
        if (content.length() > 255) {
            throw new ValidationException("Длина отзыва не должна превышать 200 символов");
        }
    }

    private void updateUseful(Long reviewId) {
        int likesCount = usefulDao.getLikesCountForReview(reviewId);
        int dislikesCount = usefulDao.getDislikesCountForReview(reviewId);
        reviewDao.updateUsefulToReview(reviewId, likesCount - dislikesCount);
    }

    private void checkFilmAndUserExist(Long filmId, Long userId) {
        filmStorage.getFilm(filmId);
        userStorage.getUserById(userId);
    }
}

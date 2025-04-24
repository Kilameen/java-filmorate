package ru.yandex.practicum.filmorate.dao.review;

public interface UsefulDao {

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void deleteMark(Long reviewId, Long userId);
}

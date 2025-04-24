package ru.yandex.practicum.filmorate.dao;

import java.util.Map;
import java.util.Set;

public interface LikeDao {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Map<Long, Set<Long>> getAllUsersWithLikes();
}
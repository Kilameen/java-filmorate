package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface LikeDao {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Long> getFilmLikes(Film film);

    Map<Long, Set<Long>> getAllUsersWithLikes();
}

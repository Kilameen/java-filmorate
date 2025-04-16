package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Rating;
import java.util.Collection;

public interface RatingDao {
    Collection<Rating> getRatingList();

    Rating getRating(Long ratingId);
}

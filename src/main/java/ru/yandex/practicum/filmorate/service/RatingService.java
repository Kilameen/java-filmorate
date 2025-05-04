package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Rating;
import java.util.Collection;

public interface RatingService {
    Collection<Rating> getAllRating();

    Rating getRatingByID(Long id);
}
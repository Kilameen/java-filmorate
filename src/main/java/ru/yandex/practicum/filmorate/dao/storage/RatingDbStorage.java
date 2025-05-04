package ru.yandex.practicum.filmorate.dao.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

@Component(value = "H2RatingDb")
@RequiredArgsConstructor
public class RatingDbStorage implements RatingDao {
    private final JdbcTemplate jdbcTemplate;
    private final RatingMapper ratingMapper;
    private static final String SELECT_RATING_BY_ID_SQL_REQUEST = "SELECT *\n" +
            "FROM rating_mpa\n" +
            "WHERE rating_id = ?;";
    private static final String SELECT_ALL_RATING_SQL_REQUEST = "SELECT *\n" +
            "FROM rating_mpa\n" +
            "ORDER BY rating_id;";

    @Override
    public Collection<Rating> getRatingList() {
        return jdbcTemplate.query(SELECT_ALL_RATING_SQL_REQUEST, ratingMapper);
    }

    @Override
    public Rating getRating(Long ratingId) {
        return jdbcTemplate.query(SELECT_RATING_BY_ID_SQL_REQUEST, ratingMapper, ratingId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("MPA с id " + ratingId + " не найден"));
    }
}

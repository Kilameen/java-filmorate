package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.util.Collection;

@Component
@Repository
@RequiredArgsConstructor
public class RatingDbStorage implements RatingDao{
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_REQUEST_DIRECTORY = "src/main/resources/requests/rating/";
    private static final String SELECT_RATING_BY_ID_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getRatingById.sql");
    private static final String SELECT_ALL_RATING_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getAllRatings.sql");

    @Override
    public Collection<Rating> getRatingList() {
        return jdbcTemplate.query(SELECT_ALL_RATING_SQL_REQUEST, new RatingMapper());
    }

    @Override
    public Rating getRating(Long ratingId) {
        return jdbcTemplate.query(SELECT_RATING_BY_ID_SQL_REQUEST, new RatingMapper(), ratingId)
                .stream().findAny().orElse(null);
    }
}

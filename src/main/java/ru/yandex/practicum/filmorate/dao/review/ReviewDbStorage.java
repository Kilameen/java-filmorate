package ru.yandex.practicum.filmorate.dao.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Component(value = "H2ReviewDb")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;

    private static final String INSERT_REVIEW_SQL_REQUEST = "INSERT INTO reviews (content, is_positive, film_id, user_id, useful)\n" +
            "VALUES (?, ?, ?, ?, 0);"; //В функциональности написано: При создании отзыва рейтинг равен нулю.
    private static final String UPDATE_REVIEW_SQL_REQUEST = "UPDATE reviews SET content=?, is_positive=?, film_id=?, user_id=? WHERE review_id=?;";
    private static final String SELECT_REVIEW_BY_ID_SQL_REQUEST = "SELECT * FROM reviews WHERE review_id=?";
    private static final String DELETE_REVIEW_SQL_REQUEST = "DELETE FROM reviews WHERE review_id = ?;";
    private static final String SELECT_ALL_REVIEWS_SQL_REQUEST = "SELECT * FROM reviews";
    private static final String SELECT_REVIEWS_BY_FILM_ID_SQL_REQUEST = "SELECT * FROM reviews WHERE film_id=? ORDER BY useful DESC LIMIT ?";
    private static final String CHECK_REVIEW_IS_EXIST = "SELECT EXISTS(SELECT 1 FROM reviews WHERE review_id = ?)";
    private static final String UPDATE_USEFUL_FOR_REVIEW_SQL_REQUEST = "UPDATE reviews SET useful=? WHERE review_id=?;";

    @Override
    public Review create(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_REVIEW_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, review.getContent());
            preparedStatement.setBoolean(2, review.getIsPositive());
            preparedStatement.setLong(3, review.getFilmId());
            preparedStatement.setLong(4, review.getUserId());
            return preparedStatement;
        }, keyHolder);
        return getReviewById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Review update(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(UPDATE_REVIEW_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, review.getContent());
            preparedStatement.setBoolean(2, review.getIsPositive());
            preparedStatement.setLong(3, review.getFilmId());
            preparedStatement.setLong(4, review.getUserId());
            preparedStatement.setLong(5, review.getId());
            return preparedStatement;
        }, keyHolder);
        return getReviewById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_REVIEW_SQL_REQUEST);
            preparedStatement.setLong(1, id);
            return preparedStatement;
        });
    }

    @Override
    public Collection<Review> findAll() {
        return jdbcTemplate.query(SELECT_ALL_REVIEWS_SQL_REQUEST, reviewMapper);
    }

    @Override
    public Review getReviewById(Long id) {
        return jdbcTemplate.query(SELECT_REVIEW_BY_ID_SQL_REQUEST, reviewMapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
    }

    @Override
    public Collection<Review> getReviewsByFilmId(Long id, int count) {
        return jdbcTemplate.query(SELECT_REVIEWS_BY_FILM_ID_SQL_REQUEST, reviewMapper, id, count);
    }

    @Override
    public Collection<Review> getAllReviews() {
        return jdbcTemplate.query(SELECT_ALL_REVIEWS_SQL_REQUEST, reviewMapper);
    }

    @Override
    public boolean isReviewExist(Long id) {
        try {
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(CHECK_REVIEW_IS_EXIST, Boolean.class, id));
        } catch (Exception ex) {
            throw new RuntimeException("Во время поиска отзыва с id " + id + "произошла ошибка!");
        }
    }

    @Override
    public void updateUsefulToReview(Long reviewId, int useful) {
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(UPDATE_USEFUL_FOR_REVIEW_SQL_REQUEST);
            preparedStatement.setInt(1, useful);
            preparedStatement.setLong(2, reviewId);
            return preparedStatement;
        });
    }
}

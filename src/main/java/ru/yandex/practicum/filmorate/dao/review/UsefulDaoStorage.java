package ru.yandex.practicum.filmorate.dao.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component(value = "H2UsefulDb")
@RequiredArgsConstructor
public class UsefulDaoStorage implements UsefulDao {

    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_LIKE_SQL_REQUEST = "INSERT INTO review_likes (review_id, user_id, is_positive)\n" +
            "VALUES (?, ?, TRUE);";
    private static final String DELETE_MARK_SQL_REQUEST = "DELETE\n" +
            "FROM review_likes\n" +
            "WHERE review_id = ? AND user_id = ?;";
    private static final String INSERT_DISLIKE_SQL_REQUEST = "INSERT INTO review_likes (review_id, user_id, is_positive)\n" +
            "VALUES (?, ?, FALSE);";

    @Override
    public void addLike(Long reviewId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_LIKE_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, reviewId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_DISLIKE_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, reviewId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
    }

    @Override
    public void deleteMark(Long reviewId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_MARK_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, reviewId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
    }
}

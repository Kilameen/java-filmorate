package ru.yandex.practicum.filmorate.dao.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@Component(value = "H2UsefulDb")
@RequiredArgsConstructor
public class UsefulDbStorage implements UsefulDao {

    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_LIKE_SQL_REQUEST = "INSERT INTO review_likes (review_id, user_id, is_positive)\n" +
            "VALUES (?, ?, TRUE);";
    private static final String INSERT_DISLIKE_SQL_REQUEST = "INSERT INTO review_likes (review_id, user_id, is_positive)\n" +
            "VALUES (?, ?, FALSE);";

    private static final String UPDATE_LIKE_SQL_REQUEST = "UPDATE review_likes SET is_positive = TRUE WHERE review_id = ? AND user_id = ?;";
    private static final String UPDATE_DISLIKE_SQL_REQUEST = "UPDATE review_likes SET is_positive = FALSE WHERE review_id = ? AND user_id = ?;";

    private static final String DELETE_LIKE_SQL_REQUEST = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = TRUE;";
    private static final String DELETE_DISLIKE_SQL_REQUEST = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = FALSE;";
    private static final String DELETE_ALL_MARK_SQL_REQUEST = "DELETE FROM review_likes WHERE review_id = ?;";

    private static final String GET_LIKES_COUNT_SQL_REQUEST = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_positive = TRUE;";
    private static final String GET_DISLIKES_COUNT_SQL_REQUEST = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_positive = FALSE;";

    private static final String CHECK_LIKE_IS_EXIST = "SELECT EXISTS (SELECT 1 FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = TRUE);";
    private static final String CHECK_DISLIKE_IS_EXIST = "SELECT EXISTS (SELECT 1 FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = FALSE);";


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
        log.info("Пользователь с id {} поставил лайк отзыву с id {}", userId, reviewId);
    }

    @Override
    public void changeDislikeToLike(Long reviewId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(UPDATE_LIKE_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, reviewId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
        log.info("Пользователь с id {} убрал дизлайк и поставил лайк отзыву с id {}", userId, reviewId);
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
        log.info("Пользователь с id {} поставил дизлайк отзыву с id {}", userId, reviewId);
    }

    @Override
    public void changeLikeToDislike(Long reviewId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(UPDATE_DISLIKE_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, reviewId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
        log.info("Пользователь с id {} убрал лайк и поставил дизлайк отзыву с id {}", userId, reviewId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_LIKE_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, reviewId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
        log.info("Пользователь с id {} удалил свой лайк отзыву с id {}", userId, reviewId);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_DISLIKE_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, reviewId);
            preparedStatement.setLong(2, userId);
            return preparedStatement;
        }, keyHolder);
        log.info("Пользователь с id {} удалил свой дизлайк отзыву с id {}", userId, reviewId);
    }

    @Override
    public void deleteAllMarks(Long reviewId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_ALL_MARK_SQL_REQUEST,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, reviewId);
            return preparedStatement;
        }, keyHolder);
        log.info("Удалены все оценки отзыву с id {}", reviewId);
    }

    @Override
    public int getLikesCountForReview(Long reviewId) {
        log.info("Запрошено количество лайков для отзыва с id {}", reviewId);
        return Optional.ofNullable(
                        jdbcTemplate.queryForObject(GET_LIKES_COUNT_SQL_REQUEST, Integer.class, reviewId)
                )
                .orElseThrow(() -> new NotFoundException("Отзыв не найден!"));
    }

    @Override
    public int getDislikesCountForReview(Long reviewId) {
        log.info("Запрошено количество дизлайков для отзыва с id {}", reviewId);
        return Optional.ofNullable(
                        jdbcTemplate.queryForObject(GET_DISLIKES_COUNT_SQL_REQUEST, Integer.class, reviewId)
                )
                .orElseThrow(() -> new NotFoundException("Отзыв не найден!"));
    }

    @Override
    public boolean isLikeExist(Long reviewId, Long userId) {
        return jdbcTemplate.query(CHECK_LIKE_IS_EXIST, (rs, rowNum) -> rs.getBoolean(1), reviewId, userId)
                .stream()
                .findAny()
                .orElse(false);
    }

    @Override
    public boolean isDislikeExist(Long reviewId, Long userId) {
       return jdbcTemplate.query(CHECK_DISLIKE_IS_EXIST, (rs, rowNum) -> rs.getBoolean(1), reviewId, userId)
                .stream()
                .findAny()
                .orElse(false);
    }
}

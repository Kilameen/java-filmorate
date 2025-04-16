package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Component(value = "H2FriendDb")
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private static final String INSERT_USER_FRIEND_SQL_REQUEST = "INSERT INTO friendship (user_id, friend_id, status)\n" +
            "VALUES (?, ?, ?);";
    private static final String DELETE_USER_FRIEND_SQL_REQUEST = "DELETE\n" +
            "FROM friendship\n" +
            "WHERE user_id = ? AND friend_id = ?;";
    private static final String SELECT_ALL_USER_FRIENDS_SQL_REQUEST = "SELECT users.*\n" +
            "FROM users\n" +
            "         INNER JOIN friendship ON users.user_id = friendship.friend_id\n" +
            "WHERE friendship.user_id = ?;";
    private static final String SELECT_STATUS_FRIENDS_SQL_REQUEST = "SELECT COUNT(*) AS status\n" +
            "FROM friendship\n" +
            "WHERE (user_id = ? AND friend_id = ?)\n" +
            "   OR (user_id = ? AND friend_id = ?)\n" +
            "HAVING COUNT(*) = 2;";
    private static final String SELECT_MUTUAL_FRIENDS_SQL_REQUEST = "SELECT users.*\n" +
            "FROM users\n" +
            "         INNER JOIN friendship ON users.user_id = friendship.friend_id\n" +
            "WHERE friendship.user_id = ?\n" +
            "\n" +
            "INTERSECT\n" +
            "\n" +
            "SELECT users.*\n" +
            "FROM users\n" +
            "         INNER JOIN friendship ON users.user_id = friendship.friend_id\n" +
            "WHERE friendship.user_id = ?;";

    @Override
    public void addFriend(Long userIdOne, Long userIdTwo) {

        if (isFriend(userIdTwo, userIdOne)) {
            jdbcTemplate.update(INSERT_USER_FRIEND_SQL_REQUEST, userIdOne, userIdTwo, true);
            jdbcTemplate.update(INSERT_USER_FRIEND_SQL_REQUEST, userIdTwo, userIdOne, true);
        } else {
            KeyHolder keyHolderOne = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection
                        .prepareStatement(INSERT_USER_FRIEND_SQL_REQUEST,
                                Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, userIdOne);
                preparedStatement.setLong(2, userIdTwo);
                preparedStatement.setBoolean(3, false);
                return preparedStatement;
            }, keyHolderOne);
        }
    }

    @Override
    public void deleteFriend(Long userIdOne, Long userIdTwo) {
        jdbcTemplate.update(DELETE_USER_FRIEND_SQL_REQUEST, userIdOne, userIdTwo);
    }

    @Override
    public Boolean isFriend(Long userIdOne, Long userIdTwo) {
        return jdbcTemplate.query(SELECT_STATUS_FRIENDS_SQL_REQUEST,
                        (rs, rowNum) -> rs.getObject("status", Boolean.class), userIdOne, userIdTwo, userIdTwo, userIdOne)
                .stream().anyMatch(Objects::nonNull);
    }

    @Override
    public Collection<User> getAllUserFriends(Long userId) {
        return jdbcTemplate.query(SELECT_ALL_USER_FRIENDS_SQL_REQUEST, userMapper, userId);
    }

    @Override
    public Collection<User> getMutualFriends(Long userIdOne, Long userIdTwo) {
        return jdbcTemplate.query(SELECT_MUTUAL_FRIENDS_SQL_REQUEST, userMapper, userIdOne, userIdTwo);
    }
}
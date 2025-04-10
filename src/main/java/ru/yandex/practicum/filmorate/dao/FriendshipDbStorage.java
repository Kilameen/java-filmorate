package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_REQUEST_DIRECTORY = "src/main/resources/requests/user/friendship/";
    private static final String INSERT_USER_FRIEND_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "addUserFriend.sql");
    private static final String DELETE_USER_FRIEND_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "deleteUserFriend.sql");
    private static final String SELECT_ALL_USER_FRIENDS_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getAllUserFriend.sql");
    private static final String SELECT_STATUS_FRIENDS_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getGeneralStatusOfFriends.sql");
    private static final String SELECT_MUTUAL_FRIENDS_SQL_REQUEST = Reader.readString(SQL_REQUEST_DIRECTORY + "getMutualFriends.sql");

    @Override
    public void addFriend(Long userIdOne, Long userIdTwo) {
        // Сначала проверяем, есть ли уже запрос на дружбу от userIdTwo к userIdOne.
        if (isFriend(userIdTwo, userIdOne)) {
            // Если запрос есть, обновляем статус дружбы в обеих записях.
            jdbcTemplate.update(INSERT_USER_FRIEND_SQL_REQUEST, userIdOne, userIdTwo, true);
            jdbcTemplate.update(INSERT_USER_FRIEND_SQL_REQUEST, userIdTwo, userIdOne, true);
        } else {
            // Если запроса нет, создаем новый запрос на дружбу только для userIdOne -> userIdTwo
            KeyHolder keyHolderOne = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection
                        .prepareStatement(INSERT_USER_FRIEND_SQL_REQUEST,
                                Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setLong(1, userIdOne);
                preparedStatement.setLong(2, userIdTwo);
                preparedStatement.setBoolean(3, false); // Изначально статус - "не подтверждено".
                return preparedStatement;
            }, keyHolderOne);
        }
    }

    @Override
    public void deleteFriend(Long userIdOne, Long userIdTwo) {
        jdbcTemplate.update(DELETE_USER_FRIEND_SQL_REQUEST, userIdOne, userIdTwo);
        jdbcTemplate.update(DELETE_USER_FRIEND_SQL_REQUEST, userIdTwo, userIdOne);
    }

    @Override
    public Boolean isFriend(Long userIdOne, Long userIdTwo) {
        return jdbcTemplate.query(SELECT_STATUS_FRIENDS_SQL_REQUEST,
                        (rs, rowNum) -> rs.getObject("status", Boolean.class), userIdOne, userIdTwo, userIdTwo, userIdOne)
                .stream().anyMatch(Objects::nonNull);
    }

    public Collection<User> getAllUserFriends(Long userId) {
        return jdbcTemplate.query(SELECT_ALL_USER_FRIENDS_SQL_REQUEST, new UserMapper(), userId);
    }

    @Override
    public Collection<User> getMutualFriends(Long userIdOne, Long userIdTwo) {
        return jdbcTemplate.query(SELECT_MUTUAL_FRIENDS_SQL_REQUEST, new UserMapper(), userIdOne, userIdTwo);
    }
}
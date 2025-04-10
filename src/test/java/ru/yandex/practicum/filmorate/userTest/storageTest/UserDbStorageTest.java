package ru.yandex.practicum.filmorate.userTest.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    User testUser = new User(
            null,
            "testing@yandex.ru",
            "testingLogin",
            "testingName",
            LocalDate.of(2025, 1, 1));

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update(Reader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/schema.sql"));
        jdbcTemplate.update(Reader.readString("src/test/resources/dataSource.sql"));
    }

    @Test
    void getAllTest() {
        Collection<Long> idCollection = userStorage.findAll().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(Arrays.asList(1L, 2L, 3L), idCollection);
    }

    @Test
    void getById() {
        User user = new User(1L,
                "test@yandex.ru",
                "testLogin",
                "testName",
                LocalDate.of(1998, 3, 8));
        Assertions.assertEquals(user, userStorage.getUserById(1L));
    }

    @Test
    void addTest() {
        testUser.setId(4L);
        Assertions.assertEquals(testUser, userStorage.create(testUser));
    }

    @Test
    void updateTest() {
        testUser.setId(1L);
        Assertions.assertEquals(testUser, userStorage.update(testUser));
    }
}
package ru.yandex.practicum.filmorate.filmTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.RatingController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class RatingControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RatingController ratingController;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update(Reader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/schema.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/data.sql"));
    }

    @Test
    void testGetAllRatings() {
        Collection<Rating> ratings = ratingController.getRatings();
        assertNotNull(ratings, "Список рейтингов не должен быть null");
        assertFalse(ratings.isEmpty(), "Список рейтингов не должен быть пустым");
    }

    @Test
    void testGetRatingById() {
        Rating rating = ratingController.getRatingById(1L);
        assertNotNull(rating, "Рейтинг не должен быть null");
        assertEquals(1L, rating.getId(), "ID рейтинга должен совпадать");
    }

    @Test
    void testGetNonExistingRatingById() {
        assertThrows(ru.yandex.practicum.filmorate.exception.NotFoundException.class, () -> {
            ratingController.getRatingById(999L);
        });
    }
}
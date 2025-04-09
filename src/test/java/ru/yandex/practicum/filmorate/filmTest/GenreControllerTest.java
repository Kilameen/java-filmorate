package ru.yandex.practicum.filmorate.filmTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreControllerTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update(Reader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/requests/schema.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/requests/data.sql"));
    }

    @Autowired
    private GenreController genreController;

    @Test
    public void testGetGenres() {
        Collection<Genre> genres = genreController.getGenres();
        assertNotNull(genres, "Список жанров не должен быть null");
        assertFalse(genres.isEmpty(), "Список жанров не должен быть пустым");
    }

    @Test
    public void testGetGenreById() {
        Genre genre = genreController.getGenreByID(1L);
        assertNotNull(genre, "Жанр не должен быть null");
        assertEquals(1L, genre.getId(), "ID жанра должен совпадать");
    }
}
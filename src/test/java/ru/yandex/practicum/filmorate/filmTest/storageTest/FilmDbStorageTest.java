package ru.yandex.practicum.filmorate.filmTest.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.utils.Reader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    Film testFilm = new Film(null,
            "Test Film",
            "Test Description",
            LocalDate.of(1993, 1, 25),
            220,
            0L,
            new Rating(1L, "G"),
            null);

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update(Reader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/schema.sql"));
        jdbcTemplate.update(Reader.readString("src/test/resources/dataSource.sql"));
    }

    @Test
    void getAllTest() {
        Collection<Long> idCollection = filmStorage.findAll().stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Assertions.assertEquals(Arrays.asList(1L, 2L, 3L), idCollection);
    }


    @Test
    void getById() {
        Film film = new Film(1L,
                "Test film2",
                "Test description2",
                LocalDate.of(1998, 3, 8),
                100,
                1L,
                new Rating(3L, "PG-13"),
                null);
        Assertions.assertEquals(film, filmStorage.getFilm(1L));
    }

    @Test
    void addTest() {
        testFilm.setId(4L);
        testFilm.setLikes(0L);
        Assertions.assertEquals(testFilm, filmStorage.create(testFilm));
    }

    @Test
    void updateTest() {
        testFilm.setId(1L);
        testFilm.setLikes(1L);
        Assertions.assertEquals(testFilm, filmStorage.update(testFilm));
    }
}
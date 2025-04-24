package ru.yandex.practicum.filmorate.directorTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DirectorDbStorageTest {
    @Autowired
    private DirectorDbStorage directorDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM films_directors");
        jdbcTemplate.execute("DELETE FROM directors");
    }

    @AfterEach
    void tearDown() {
        // Очистка таблиц после каждого теста
        jdbcTemplate.execute("DELETE FROM films_directors");
        jdbcTemplate.execute("DELETE FROM directors");
    }

    @Test
    void createDirector_ShouldInsertAndReturnDirector() {
        // Arrange
        Director director = new Director(null, "Test Director");

        Director createdDirector = directorDbStorage.createDirector(director);

        assertNotNull(createdDirector.getId());
        assertEquals("Test Director", createdDirector.getName());

        List<Director> directors = jdbcTemplate.query("SELECT * FROM directors", (rs, rowNum) ->
                new Director(rs.getLong("director_id"), rs.getString("name")));
        assertEquals(1, directors.size());
        assertEquals("Test Director", directors.get(0).getName());
    }

    @Test
    void updateDirector_ShouldUpdateAndReturnDirector() {
        // Arrange
        Director initialDirector = directorDbStorage.createDirector(new Director(null, "Initial Director"));
        Director updatedDirector = new Director(initialDirector.getId(), "Updated Director");

        // Act
        Director result = directorDbStorage.updateDirector(updatedDirector);

        // Assert
        assertEquals("Updated Director", result.getName());

        // Verify in DB
        List<Director> directors = jdbcTemplate.query("SELECT * FROM directors", (rs, rowNum) ->
                new Director(rs.getLong("director_id"), rs.getString("name")));
        assertEquals(1, directors.size());
        assertEquals("Updated Director", directors.get(0).getName());
    }

    @Test
    void updateDirector_ShouldThrowException_WhenDirectorDoesNotExist() {
        // Arrange
        Director nonExistentDirector = new Director(999L, "Updated Director");

        // Act & Assert
        InternalServerException exception = assertThrows(InternalServerException.class, () ->
                directorDbStorage.updateDirector(nonExistentDirector));
        assertEquals("Не удалось обновить данные для режиссера с ID 999", exception.getMessage());
    }

    @Test
    void deleteDirector_ShouldDeleteDirectorFromDatabase() {
        Director director = directorDbStorage.createDirector(new Director(null, "Test Director"));
        directorDbStorage.deleteDirector(director.getId());

        List<Director> directors = jdbcTemplate.query("SELECT * FROM directors", (rs, rowNum) ->
                new Director(rs.getLong("director_id"), rs.getString("name")));
        assertTrue(directors.isEmpty());
    }

    @Test
    void getFilmDirectors_ShouldReturnListOfDirectors_WhenFilmHasDirectors() {
        Director director1 = directorDbStorage.createDirector(new Director(null, "Director 1"));
        Director director2 = directorDbStorage.createDirector(new Director(null, "Director 2"));
        jdbcTemplate.update("INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)", 1L, director1.getId());
        jdbcTemplate.update("INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)", 1L, director2.getId());
        List<Director> directors = directorDbStorage.getFilmDirectors(1L);

        // Assert
        assertEquals(2, directors.size());
        assertEquals("Director 1", directors.get(0).getName());
        assertEquals("Director 2", directors.get(1).getName());
    }

    @Test
    void getFilmDirectors_ShouldReturnEmptyList_WhenFilmHasNoDirectors() {
        // Act
        List<Director> directors = directorDbStorage.getFilmDirectors(999L);

        assertTrue(directors.isEmpty());
    }
}

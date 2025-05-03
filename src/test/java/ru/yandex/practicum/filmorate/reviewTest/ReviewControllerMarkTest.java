package ru.yandex.practicum.filmorate.reviewTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.utils.Reader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Тесты для проверки ReviewController. Поведение оценок")
public class ReviewControllerMarkTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update(Reader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/schema.sql"));
        jdbcTemplate.update(Reader.readString("src/test/resources/dataSource.sql"));
    }

    @Test
    @DisplayName("Добавить лайк. Успешно")
    void testAddLike_Success() throws Exception {
        Long reviewId = 4L;
        Long userId = 1L;

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Поставить лайк вместо дизлайка. Успешно")
    void testChangeDislikeToLike_Success() throws Exception {
        Long reviewId = 2L;
        Long userId = 3L;

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Добавить лайк. Отзыв не найден")
    void testAddLike_ReviewNotFound() throws Exception {
        Long reviewId = 999L;
        Long userId = 1L;

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Добавить лайк. Пользователь не найден")
    void testAddLike_UserNotFound() throws Exception {
        Long reviewId = 1L;
        Long userId = 999L;

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Добавить дизлайк. Успешно")
    void testAddDislike_Success() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Добавить дизлайк. Отзыв не найден")
    void testAddDislike_ReviewNotFound() throws Exception {
        Long reviewId = 999L;
        Long userId = 2L;

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Добавить дизлайк. Пользователь не найден")
    void testAddDislike_UserNotFound() throws Exception {
        Long reviewId = 1L;
        Long userId = 999L;

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить лайк. Успешно")
    void testDeleteLike_Success() throws Exception {
        Long reviewId = 1L;
        Long userId = 2L;

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удалить лайк. Отзыв не найден")
    void testDeleteLike_ReviewNotFound() throws Exception {
        Long reviewId = 999L;
        Long userId = 1L;

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить лайк. Пользователь не найден")
    void testDeleteLike_UserNotFound() throws Exception {
        Long reviewId = 1L;
        Long userId = 999L;

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить лайк. Лайк не найден")
    void testDeleteLike_LikeNotFound() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить дизлайк. Успешно")
    void testDeleteDislike_Success() throws Exception {
        Long reviewId = 2L;
        Long userId = 3L;

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удалить дизлайк. Отзыв не найден")
    void testDeleteDislike_ReviewNotFound() throws Exception {
        Long reviewId = 999L;
        Long userId = 1L;

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить дизлайк. Пользователь не найден")
    void testDeleteDislike_UserNotFound() throws Exception {
        Long reviewId = 1L;
        Long userId = 999L;

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить дизлайк. Дизлайк не найден")
    void testDeleteDislike_DislikeNotFound() throws Exception {
        Long reviewId = 1L;
        Long userId = 2L;

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());
    }

}

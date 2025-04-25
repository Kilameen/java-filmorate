package ru.yandex.practicum.filmorate.reviewTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.utils.Reader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc // Настройка MockMvc для тестирования контроллеров
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Очищает контекст после каждого теста
@DisplayName("Тесты для проверки ReviewController. Сами отзывы")
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    Review review;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update(Reader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/schema.sql"));
        jdbcTemplate.update(Reader.readString("src/test/resources/dataSource.sql"));

        review = new Review();
        review.setUserId(1L);
        review.setFilmId(1L);
        review.setContent("Отличный фильм!");
        review.setIsPositive(true);
        review.setUseful(5);
    }

    @Test
    @DisplayName("Создать новый отзыв. Успешно")
    void testCreateReview_Success() throws Exception {
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.content").value("Отличный фильм!"))
                .andExpect(jsonPath("$.isPositive").value(true))
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    @DisplayName("Создать новый отзыв. Превышена длина отзыва")
    void testCreateReview_ExceedsMaxLength() throws Exception {
        review.setContent("a".repeat(256));

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновить отзыв. Успешно")
    void testUpdateReview_Success() throws Exception {
        review.setId(1L);
        review.setContent("Обновленный отзыв");
        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.content").value("Обновленный отзыв"))
                .andExpect(jsonPath("$.isPositive").value(true))
                .andExpect(jsonPath("$.useful").value(2));

    }

    @Test
    @DisplayName("Обновить отзыв. Отзыв не найден")
    void testUpdateReview_NotFound() throws Exception {
        review.setId(10L);

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удалить отзыв. Успешно")
    void testDeleteReview_Success() throws Exception {

        mockMvc.perform(delete("/reviews/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удалить отзыв. Отзыв не найден")
    void testDeleteReview_NotFound() throws Exception {
        mockMvc.perform(delete("/reviews/{id}", 10L))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Получить отзыв. Успешно")
    void testGetReview_Success() throws Exception {
        mockMvc.perform(get("/reviews/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.content").value("Фильм мне понравился. Отличный!"))
                .andExpect(jsonPath("$.isPositive").value(true))
                .andExpect(jsonPath("$.useful").value(2));
    }

    @Test
    @DisplayName("Получить отзыв. Отзыв не найден")
    void testGetReview_NotFound() throws Exception {
        mockMvc.perform(get("/reviews/{id}", 10L))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Получить все отзывы. Нет параметров. Успешно")
    void testGetAllReviews_NoParams_Success() throws Exception {
        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk()) // Проверяем статус ответа
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    @DisplayName("Получить все отзывы. Передано количество. Успешно")
    void testGetAllReviews_WithCount_Success() throws Exception {
        mockMvc.perform(get("/reviews")
                        .param("count", "3")) // Передаем параметр count=3
                .andExpect(status().isOk()) // Проверяем статус ответа
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    @DisplayName("Получить отзыв для фильма. Все параметры. Успешно")
    void testGetReviewsForFilm_AllParams_Success() throws Exception {
        Long filmId = 1L;

        mockMvc.perform(get("/reviews")
                        .param("filmId", String.valueOf(filmId)) // Передаем filmId=1
                        .param("count", "1")) // Передаем count=2
                .andExpect(status().isOk()) // Проверяем статус ответа
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Получить отзыв для фильма. Не передано количество. Успешно")
    void testGetReviewsForFilm_DefaultCount_Success() throws Exception {
        Long filmId = 1L;

        mockMvc.perform(get("/reviews")
                        .param("filmId", String.valueOf(filmId)))
                .andExpect(status().isOk()) // Проверяем статус ответа
                .andExpect(jsonPath("$.length()").value(2));
    }

    //@DisplayName("Получить отзыв для фильма. Не найден фильм")
    @Test
    @DisplayName("Получить отзыв для фильма. Не найден фильм")
    void testGetReviewsForFilm_FilmNotFound() throws Exception {
        Long filmId = 999L; // filmId, которого нет в базе данных

        mockMvc.perform(get("/reviews")
                        .param("filmId", String.valueOf(filmId))) // Передаем filmId=999
                .andExpect(status().isNotFound());
    }

}

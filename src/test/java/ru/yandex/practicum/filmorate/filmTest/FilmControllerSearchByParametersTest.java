package ru.yandex.practicum.filmorate.filmTest;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc // Настройка MockMvc для тестирования контроллеров
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Очищает контекст после каждого теста
@DisplayName("Тесты для проверки FilmController. Поиск по Названию или/и Режиссеру")
public class FilmControllerSearchByParametersTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update(Reader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(Reader.readString("src/main/resources/schema.sql"));
        jdbcTemplate.update(Reader.readString("src/test/resources/dataSourceForSearch.sql"));
    }

    @Test
    @DisplayName("Успешный поиск по параметру title")
    public void searchByTitleSuccess() throws Exception {
        String query = "Король";
        String by = "title";

        mockMvc.perform(get("/films/search")
                        .param("query", query)
                        .param("by", by))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Успешный поиск по параметру director")
    public void searchByDirectorSuccess() throws Exception {
        String query = "Лукас";
        String by = "director";

        mockMvc.perform(get("/films/search")
                        .param("query", query)
                        .param("by", by))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Успешный поиск по обоим параметрам.")
    public void searchByDirectorAndTitleSuccess() throws Exception {
        String query = "рд";
        String by = "director, title";

        mockMvc.perform(get("/films/search")
                        .param("query", query)
                        .param("by", by))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    @DisplayName("Поиск по параметру title. Не найдено")
    public void searchByTitleNotFound() throws Exception {
        String query = "Бур";
        String by = "title";

        mockMvc.perform(get("/films/search")
                        .param("query", query)
                        .param("by", by))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Поиск по параметру director. Не найдено")
    public void searchByDirectorNotFound() throws Exception {
        String query = "Карлос";
        String by = "director";

        mockMvc.perform(get("/films/search")
                        .param("query", query)
                        .param("by", by))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Поиск по обоим параметрам. Не найдено")
    public void searchByDirectorAndTitleNotFound() throws Exception {
        String query = "Джолли";
        String by = "director, title";

        mockMvc.perform(get("/films/search")
                        .param("query", query)
                        .param("by", by))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Поиск по несуществующим параметрам")
    public void searchByUndefinedParametersNotFound() throws Exception {
        String query = "Бур";
        String by = "parameter";

        mockMvc.perform(get("/films/search")
                        .param("query", query)
                        .param("by", by))
                .andExpect(status().isNotFound());
    }

}

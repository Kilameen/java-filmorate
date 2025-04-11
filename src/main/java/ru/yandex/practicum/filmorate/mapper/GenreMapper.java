package ru.yandex.practicum.filmorate.mapper;

import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements RowMapper<Genre> {

    @SneakyThrows
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Явно указываем кодировку при получении строки из ResultSet.
        // Это может помочь, если драйвер JDBC не использует UTF-8 по умолчанию.
        return new Genre(rs.getLong("genre_id"),
                new String(rs.getString("genre_name").getBytes("ISO-8859-1"), "UTF-8"));
    }
}
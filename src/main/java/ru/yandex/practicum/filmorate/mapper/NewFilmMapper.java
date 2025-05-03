package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NewFilmMapper implements RowMapper<Film> {

    private final Map<Long, Film> filmMap = new HashMap<>();

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("id");

        // Если фильм уже создан, используем его
        Film film = filmMap.get(filmId);
        if (film == null) {
            film = new Film();
            film.setId(filmId);
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setLikes(rs.getLong("likes"));

            // Создаем объект Rating для MPA
            Rating mpa = new Rating();
            mpa.setId(rs.getLong("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);

            // Сохраняем фильм в карте
            filmMap.put(filmId, film);
        }

        // Добавляем жанр, если он существует
        Long genreId = rs.getLong("genre_id");
        if (!rs.wasNull()) {
            Genre genre = new Genre();
            genre.setId(genreId);
            genre.setName(rs.getString("genre_name"));
            film.getGenres().add(genre);
        }

        // Добавляем режиссера, если он существует
        Long directorId = rs.getLong("director_id");
        if (!rs.wasNull()) {
            Director director = new Director();
            director.setId(directorId);
            director.setName(rs.getString("director_name"));
            film.getDirectors().add(director); // Добавляем режиссера в коллекцию
        }

        return film;
    }
}
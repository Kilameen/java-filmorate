package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class FilmMapper implements RowMapper<Film> {

    private final DirectorStorage directorStorage;
    private final GenreDbStorage genreDbStorage;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Rating(rs.getLong("rating_id"), rs.getString("rating_name")))
                .likes(rs.getLong("rate"))
                .directors(new HashSet<>(directorStorage.getFilmDirectors(rs.getLong("film_id"))))
                .genres(new HashSet<>(genreDbStorage.getFilmGenres(rs.getLong("film_id"))))
                .build();
    }
}
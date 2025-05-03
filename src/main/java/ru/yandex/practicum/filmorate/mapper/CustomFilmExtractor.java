package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomFilmExtractor {

    private final NewFilmMapper filmMapper = new NewFilmMapper();

    public List<Film> extractData(ResultSet rs) throws SQLException {
        // Карта для объединения данных о фильмах
        Map<Long, Film> filmMap = new HashMap<>();
        int rowNum = 0;

        while (rs.next()) {
            // Используем маппер для получения объекта Film
            Film film = filmMapper.mapRow(rs, rowNum++);

            // Добавляем фильм в карту или обновляем существующий
            if (!filmMap.containsKey(film.getId())) {
                filmMap.put(film.getId(), film);
            } else {
                // Объединяем жанры и режиссеров
                Film existingFilm = filmMap.get(film.getId());
                existingFilm.getGenres().addAll(film.getGenres());
                existingFilm.getDirectors().addAll(film.getDirectors());
            }
        }

        // Преобразуем значения карты в список
        return new ArrayList<>(filmMap.values());
    }
}
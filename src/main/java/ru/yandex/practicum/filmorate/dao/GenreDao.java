package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.Map;

public interface GenreDao {
    Collection<Genre> getFilmGenres(Long filmId);

    Map<Long, Collection<Genre>> getAllFilmsGenres(Collection<Long> filmIds);

    void setGenres(Long filmId, Long genreId);

    Collection<Genre> getGenres();

    Genre getGenre(Long genreId);

    void clearFilmGenres(Long filmId);
}

package ru.yandex.practicum.filmorate.service.film.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.Map;

public interface GenreService {
    Collection<Genre> getGenres();

    Genre getGenre(Long id);

    Collection<Genre> getFilmGenres(Long filmId);

    void clearFilmGenres(Long filmId);

    Map<Long, Collection<Genre>> getAllFilmsGenres(Collection<Long> filmIds);
}
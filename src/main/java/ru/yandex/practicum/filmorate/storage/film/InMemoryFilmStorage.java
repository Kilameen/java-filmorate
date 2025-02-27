package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate STARTED_REALISE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteAllFilms(Film film) {
        films.clear();
    }

    @Override
    public Film deleteFilm(Long id) {
        return films.remove(id);
    }

    @Override
    public Film update(Film updateFilm) {
        Film oldFilmInformation = films.get(updateFilm.getId());
        if (oldFilmInformation == null) {
            throw new NotFoundException("Фильма с Id = " + updateFilm.getId() + " не найдено.");
        }
        validate(updateFilm);

        if (updateFilm.getName() != null) {
            oldFilmInformation.setName(updateFilm.getName());
        }
        if (updateFilm.getDescription() != null) {
            oldFilmInformation.setDescription(updateFilm.getDescription());
        }
        if (updateFilm.getReleaseDate() != null) {
            oldFilmInformation.setReleaseDate(updateFilm.getReleaseDate());
        }
        if (updateFilm.getDuration() != null) {
            oldFilmInformation.setDuration(updateFilm.getDuration());
        }
        return oldFilmInformation;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(Film film) {
        if (films.values()
                .stream()
                .anyMatch(f -> f.getName().equals(film.getName())
                        && f.getReleaseDate().equals(film.getReleaseDate())
                        && !Objects.equals(f.getId(), film.getId()))) {
            throw new DuplicatedDataException("Фильм с таким названием и датой релиза уже существует");
        }
        if (STARTED_REALISE_DATE.isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза фильма раньше: " + STARTED_REALISE_DATE);
        }
    }
}
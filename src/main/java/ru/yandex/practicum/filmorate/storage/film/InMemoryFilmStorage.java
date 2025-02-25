package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
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
    public void deleteAllFilms(Film film){
        films.clear();
    }

    @Override
    public Film deleteFilm(Long id){
        return films.remove(id);
    }

    @Override
    public Film update(Film updateFilm) {

        if (updateFilm.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан!");
        }
        validate(updateFilm);
        if (films.containsKey(updateFilm.getId())) {
            Film oldFilmInformation = films.get(updateFilm.getId());
            oldFilmInformation.setName(updateFilm.getName());
            oldFilmInformation.setDescription(updateFilm.getDescription());
            oldFilmInformation.setReleaseDate(updateFilm.getReleaseDate());
            oldFilmInformation.setDuration(updateFilm.getDuration());

            return oldFilmInformation;
        }
        throw new NotFoundException("Фильма с Id = " + updateFilm.getId() + " не найдено.");
    }

    private void validate(Film film) {
        if (films.values()
                .stream()
                .anyMatch(f -> f.getName().equals(film.getName())
                        && f.getReleaseDate().equals(film.getReleaseDate())
                        && !Objects.equals(f.getId(), film.getId()))) {
            log.error("Фильм с названием {} и датой релиза {} уже существует", film.getName(), film.getReleaseDate());
            throw new DuplicatedDataException("Фильм с таким названием и датой релиза уже существует");
        }
        if (STARTED_REALISE_DATE.isAfter(film.getReleaseDate())) {
            log.error("Дата релиза фильма не может быть раньше: {}", STARTED_REALISE_DATE);
            throw new ValidationException("Дата релиза фильма раньше: " + STARTED_REALISE_DATE);
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
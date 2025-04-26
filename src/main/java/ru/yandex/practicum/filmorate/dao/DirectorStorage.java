package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Component
public interface DirectorStorage {

    List<Director> getDirectors();

    Optional<Director> getDirectorById(Long directorId);

    Director createDirector(Director director);

    Director updateDirector(Director newDirector);

    void deleteDirector(Long directorId);

    List<Director> getFilmDirectors(Long filmId);


}

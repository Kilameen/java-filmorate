package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    List<Director> getDirectors();

    Director getDirectorById(Long directorId);

    Director createDirector(Director director);

    Director updateDirector(Director newDirector);

    void deleteDirector(Long directorId);
}

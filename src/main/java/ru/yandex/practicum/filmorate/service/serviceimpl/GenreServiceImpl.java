package ru.yandex.practicum.filmorate.service.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class GenreServiceImpl implements GenreService {
    private final GenreDao genreDbStorage;

    @Autowired
    public GenreServiceImpl(GenreDao genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Collection<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }

    @Override
    public Genre getGenre(Long id) {
        Genre genre = genreDbStorage.getGenre(id);
        if (isNull(genre)) {
            throw new NotFoundException("Жанра с таким id не существует");
        }
        return genre;
    }
}
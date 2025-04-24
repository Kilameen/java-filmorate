package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService{

    private final DirectorStorage directorStorage;

    @Override
    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    @Override
    public Director getDirectorById(Long directorId) {
        return directorStorage.getDirectorById(directorId).orElseThrow(()-> new NotFoundException("Режиссер с id:"+directorId+" не найден"));
    }

    @Override
    public Director createDirector(Director director) {
        director = directorStorage.createDirector(director);
        return director;
    }

    @Override
    public Director updateDirector(Director newDirector) {
        Director director = directorStorage.getDirectorById(newDirector.getId()).orElseThrow(()-> new NotFoundException("Режиссер с id:"+newDirector.getId()+" не найден"));
        director = directorStorage.updateDirector(newDirector);
        director.setName(newDirector.getName());
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        directorStorage.deleteDirector(directorId);
    }
}

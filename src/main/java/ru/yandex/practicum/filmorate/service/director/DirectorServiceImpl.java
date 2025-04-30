package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    @Override
    public Director getDirectorById(Long directorId) {
        return directorStorage.getDirectorById(directorId).orElseThrow(() -> new NotFoundException("Режиссер с id:" + directorId + " не найден"));
    }

    @Override
    public Director createDirector(Director director) {
       validateDirector(director);
        director = directorStorage.createDirector(director);
        return director;
    }

    @Override
    public Director updateDirector(Director newDirector) {
        Director director = directorStorage.getDirectorById(newDirector.getId()).orElseThrow(() -> new NotFoundException("Режиссер с id:" + newDirector.getId() + " не найден"));
        validateDirector(newDirector); // Добавьте валидацию перед обновлением
        director = directorStorage.updateDirector(newDirector);
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        directorStorage.deleteDirector(directorId);
    }

    private void validateDirector(Director director){
        if (isNull(director.getName()) || director.getName().trim().isEmpty()) { // trim() убирает пробелы с начала и конца
            log.error("Имя режиссера не заполнено {}", director.getName());
            throw new ValidationException("Имя режиссера должно быть заполнено и не должно содержать только пробелы");
        }
     }
}


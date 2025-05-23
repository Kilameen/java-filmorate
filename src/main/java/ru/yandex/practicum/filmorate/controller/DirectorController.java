package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/directors")
    public List<Director> getDirectors() {
        log.info("Запрос на получение режиссеров");
        return directorService.getDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirector(@PathVariable("id") Long id) {
        log.info("Запрос на получение режиссера с id:{}", id);
        return directorService.getDirectorById(id);
    }

    @Validated(Marker.OnCreate.class)
    @PostMapping("/directors")
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на создание режиссера:{}", director);
        return directorService.createDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        log.info("Запрос на обновление режиссера:{}", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public ResponseEntity<String> deleteDirector(@PathVariable Long id) {
        log.info("Запрос на удаление режиссера с id:{}", id);
        directorService.deleteDirector(id);
        return new ResponseEntity<>("{\"message\":\"Удаление лайка прошло успешно\"}", HttpStatus.OK);
    }
}

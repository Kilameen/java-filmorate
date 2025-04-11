package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.genre.GenreService;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")

public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getGenres() {
        log.info("Получил запрос на получение всех жанров");
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreByID(@PathVariable Long id) {
        log.info("Получил запрос на получение жанра по id {}",id);
        return genreService.getGenre(id);
    }
}


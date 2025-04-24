package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/directors")
    public List<Director> getDirectors(){
        return directorService.getDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirector(@PathVariable("id") Long id){
        return directorService.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director createDirector(@RequestBody Director director){
        return directorService.createDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director){
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public ResponseEntity<String> deleteDirector(@PathVariable Long id){
        directorService.deleteDirector(id);
       return new ResponseEntity<>("{\"message\":\"Удаление лайка прошло успешно\"}", HttpStatus.OK);
    }
}

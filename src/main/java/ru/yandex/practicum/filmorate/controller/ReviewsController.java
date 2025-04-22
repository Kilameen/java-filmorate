package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewsController {

    //Добавление нового отзыва
    //POST /reviews

    //Редактирование уже имеющегося отзыва
    //PUT /reviews

    //Удаление уже имеющегося отзыва
    //DELETE /reviews/{id}

    //Получение отзыва по идентификатору
    //GET /reviews/{id}

    //Получение всех отзывов по идентификатору фильма
    //Если фильм не указан, то все
    //Если кол-во не указано, то 10
    //GET /reviews?filmId={filmId}&count={count}

    //Пользователь ставит лайк отзыву
    //PUT /reviews/{id}/like/{userId}

    //Пользователь ставит дизлайк отзыву
    //PUT /reviews/{id}/dislike/{userId}

    //Пользователь удаляет лайк/дизлайк отзыву
    //DELETE /reviews/{id}/like/{userId}

    //Пользователь удаляет дизлайк отзыву
    //DELETE /reviews/{id}/dislike/{userId}
}

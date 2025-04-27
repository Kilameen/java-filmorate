MERGE INTO genres (genre_id, genre_name) VALUES ( 1, 'Комедия' );
MERGE INTO genres (genre_id, genre_name) VALUES ( 2, 'Драма' );
MERGE INTO genres (genre_id, genre_name) VALUES ( 3, 'Мультфильм' );
MERGE INTO genres (genre_id, genre_name) VALUES ( 4, 'Триллер' );
MERGE INTO genres (genre_id, genre_name) VALUES ( 5, 'Документальный' );
MERGE INTO genres (genre_id, genre_name) VALUES ( 6, 'Боевик' );
MERGE INTO rating_mpa (rating_id, rating_name) VALUES ( 1, 'G' );
MERGE INTO rating_mpa (rating_id, rating_name) VALUES ( 2, 'PG' );
MERGE INTO rating_mpa (rating_id, rating_name) VALUES ( 3, 'PG-13' );
MERGE INTO rating_mpa (rating_id, rating_name) VALUES ( 4, 'R' );
MERGE INTO rating_mpa (rating_id, rating_name) VALUES ( 5, 'NC-17' );

INSERT INTO films (film_name, description, release_date, duration, mpa_id)
VALUES ('Король Лев', 'Мультфильм о приключениях молодого льва Симбы.', '1994-06-15', 88, 1),
       ('Король Ричард', 'История отца теннисных звезд Венеры и Серены Уильямс.', '2021-11-19', 145, 3),
       ('Звёздные войны: Эпизод IV — Новая надежда', 'Первая часть легендарной саги о борьбе добра со злом.', '1977-05-25', 121, 3),
       ('Звёздные воины', 'Пародийный мультфильм о космических пришельцах.', '2000-03-10', 90, 3),
       ('Супермен', 'История первого супергероя из комиксов DC.', '1978-12-15', 143, 3),
       ('Суперсемейка', 'Мультфильм о супергеройской семье, спасающей мир.', '2004-11-05', 115, 1),
       ('Дюна', 'Эпическая история о борьбе за контроль над пустынной планетой.', '2021-09-15', 155, 3);

INSERT INTO users (email, login, user_name, birthday)
VALUES ('test@yandex.ru', 'testLogin', 'testName', '1998-03-08'),
       ('test2@yandex.ru', 'testLogin2', 'testName2', '2024-05-09'),
       ('test3@yandex.ru', 'testLogin3', 'testName3', '1993-01-25');

INSERT INTO directors (name)
VALUES ('Роджер Аллерс'), ('Джордж Лукас'), ('Райан Куглер'), ('Брэд Бёрд'), ('Дени Вильнёв');

INSERT INTO film_likes (film_id, user_id)
VALUES (1, 1), (2, 1), (2, 2), (3, 1), (3, 2), (3, 3);

INSERT INTO friendship (user_id, friend_id, status)
VALUES (1, 2, TRUE), (2, 1, TRUE), (2, 3, TRUE), (3, 1, DEFAULT), (3, 2, TRUE);

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 3), (2, 2),
       (3, 6), (3, 4),
       (4, 1), (5, 6),
       (6, 3), (6, 1),
       (7, 2), (7, 4);

INSERT INTO films_directors (film_id, director_id)
VALUES (1, 1), (2, 3), (3, 2), (4, 1), (5, 2), (6, 4), (7, 5);

INSERT INTO reviews (content, is_positive, film_id, user_id, useful)
VALUES ('Фильм мне понравился. Отличный!', TRUE, 1, 1, 2),
       ('Нормальный фильм. Для одного раза пойдет.', TRUE, 1, 2, 1),
       ('Мне фильм не понравился. Было скучно', FALSE, 2, 1, 2),
       ('Неплохой фильм.', TRUE, 2, 3, 0);

INSERT INTO review_likes (review_id, user_id, is_positive)
VALUES (1,2,TRUE),
       (1,3,TRUE),
       (2,3,FALSE),
       (3,2,TRUE),
       (3,3,TRUE);

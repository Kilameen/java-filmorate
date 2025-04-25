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
VALUES ('Test film2', 'Test description2', '1998-03-08', 100, 3),
       ('Test film3', 'Test description3', '2024-05-09', 178, 3),
       ('Test film4', 'Test description4', '1993-01-25', 154, 4);

INSERT INTO users (email, login, user_name, birthday)
VALUES ('test@yandex.ru', 'testLogin', 'testName', '1998-03-08'),
       ('test2@yandex.ru', 'testLogin2', 'testName2', '2024-05-09'),
       ('test3@yandex.ru', 'testLogin3', 'testName3', '1993-01-25');

INSERT INTO film_likes (film_id, user_id)
VALUES (1, 1), (2, 1), (2, 2), (3, 1), (3, 2), (3, 3);

INSERT INTO friendship (user_id, friend_id, status)
VALUES (1, 2, TRUE), (2, 1, TRUE), (2, 3, TRUE), (3, 1, DEFAULT), (3, 2, TRUE);

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1), (2, 2), (3, 1), (3, 4);

INSERT INTO reviews (content, is_positive, film_id, user_id, useful)
VALUES ('Фильм мне понравился. Отличный!', TRUE, 1, 1, 2),
       ('Нормальный фильм. Для одного раза пойдет.', TRUE, 1, 2, 1),
       ('Мне фильм не понравился. Было скучно', FALSE, 2, 1, 2),
       ('Неплохой фильм.', TRUE, 2, 3, 0);

INSERT INTO review_likes (review_id, user_id)
VALUES (1,2),
       (1,3),
       (3,2),
       (3,3);

INSERT INTO review_dislikes (review_id, user_id)
VALUES (2,3);

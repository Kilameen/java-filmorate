SELECT genres.genre_id, genre_name
FROM film_genres
         LEFT JOIN genres ON film_genres.genre_id = genres.genre_id
WHERE film_id = ?
ORDER BY genres.genre_id
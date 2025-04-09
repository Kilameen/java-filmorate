SELECT films.*, rating.*, COUNT(film_likes.user_id) AS rate
FROM films
LEFT JOIN rating ON films.mpa_id = rating.rating_id
LEFT JOIN film_likes ON films.film_id = film_likes.film_id
GROUP BY films.film_id
ORDER BY films.film_id
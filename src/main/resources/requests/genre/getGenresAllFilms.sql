SELECT fg.film_id, g.*
FROM film_genres fg
JOIN genres g ON fg.genre_id = g.genre_id
WHERE fg.film_id IN (:filmIds);
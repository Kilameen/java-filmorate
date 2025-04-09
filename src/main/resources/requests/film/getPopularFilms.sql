SELECT f.*, r.rating_id, r.rating_name, COUNT(fl.user_id) AS rate
FROM films f
LEFT JOIN rating r ON f.mpa_id = r.rating_id
LEFT JOIN film_likes fl ON f.film_id = fl.film_id
GROUP BY f.film_id, r.rating_id, r.rating_name
ORDER BY rate DESC
LIMIT ?;

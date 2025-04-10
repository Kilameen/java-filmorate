SELECT f.*, r.rating_name, r.rating_id, COUNT(fl.user_id) AS rate
FROM films AS f
LEFT JOIN rating AS r ON f.mpa_id = r.rating_id
LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id
GROUP BY f.film_id, r.rating_id
ORDER BY f.film_id;

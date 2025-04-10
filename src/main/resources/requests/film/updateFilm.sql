UPDATE films
SET film_name = ?,
    description = ?,
    release_date = ?,
    duration = ?,
    mpa_id = ?
WHERE film_id = ?;
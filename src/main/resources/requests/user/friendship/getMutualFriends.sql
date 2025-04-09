SELECT users.*
FROM users
         INNER JOIN friendship ON users.user_id = friendship.friend_id
WHERE friendship.user_id = ?

INTERSECT

SELECT users.*
FROM users
         INNER JOIN friendship ON users.user_id = friendship.friend_id
WHERE friendship.user_id = ?
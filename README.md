### Примеры SQL-запросов

#### Для работы с базой данных приложения Filmorate можно использовать следующие примеры запросов:
**Получение списка всех пользователей:**
```
SELECT *
FROM users;
```

**Получение ТОП-10 популярных фильмов:**
```
SELECT f.name
FROM films AS f
LEFT JOIN likes AS l ON f.id = l.film_id
GROUP BY f.id
ORDER BY COUNT(l.user_id) DESC
LIMIT 10;
```

**Получение всех жанров для концретного фильма по id:**
```
SELECT g.name
FROM genre AS g
INNER JOIN film_genre AS fg ON g.id = fg.genre_id
WHERE fg.film_id = 5;
```

**Получение списка общих друзей:**
```
SELECT u.name
FROM users AS u
INNER JOIN friends AS f1 ON u.id = f1.friend_id
INNER JOIN friends AS f2 ON f1.friend_id = f2.friend_id
WHERE f1.user_id = 1
AND f2.user_id = 2
AND f1.is_confirmed = true
AND f2.is_confirmed = true;
```

![ER-диаграмма базы данных для приложения Filmorate. На ней изображена связь между классами User, Films, Genre, Likes.](filmorate-image-BD/filmorate-BD.png)
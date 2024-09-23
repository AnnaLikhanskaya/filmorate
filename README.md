# java-filmorate

Сервис, на основе REST API, который позволяет пользователям оценивать фильмы и делиться впечатлением о них, возвращяя ТОП-фильмы рекомендованные к просмотру.
Теперь ни вам, ни вашим друзьям не придётся долго размышлять, что посмотреть вечером.

получение всех фильмов:

```sql
SELECT * FROM films
```

получение фильма по id:
```sql
SELECT *
FROM movie
WHERE movie_id = <id>
```

получить фильм с id=2:
```sql
 SELECT *
    FROM films
    WHERE film_id = 2;

```


получение топ N наиболее популярных фильмов:

```sql

SELECT * 
FROM films WHERE film_id IN
(SELECT film_id 
FROM likes GROUP BY film_id ORDER BY COUNT(film_id) DESC LIMIT 10)
```

получение всех пользователей:

```sql

SELECT * FROM users
```

получить пользователя с id=5:
```sql
 SELECT *
    FROM users
    WHERE user_id = 5;


```

получение списка общих друзей с другим пользователем:
```sql
SELECT friend_id FROM friendship WHERE user_id IN (1,4) GROUP BY friend_id 
HAVING COUNT(friend_id)>1)
```


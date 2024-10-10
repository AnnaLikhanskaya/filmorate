CREATE TABLE IF NOT EXISTS DIRECTOR (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR);

CREATE TABLE IF NOT EXISTS MPA (
                                   id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                    name VARCHAR);

CREATE TABLE IF NOT EXISTS GENRES (
                                     id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     name VARCHAR);

CREATE TABLE IF NOT EXISTS FILMS (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    name varchar(255) NOT NULL,
                                    description varchar(200),
                                    releasedate date NOT NULL,
                                    duration BIGINT NOT NULL,
                                    mpa_id INT REFERENCES mpa(id) NOT NULL);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR (
                                          id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                          director_id integer REFERENCES director (id) ON DELETE CASCADE NOT NULL,
                                          film_id integer REFERENCES films (id) ON DELETE CASCADE NOT NULL);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
                                          id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                          genre_id integer REFERENCES genres (id) ON DELETE CASCADE NOT NULL,
                                          film_id integer REFERENCES films (id) ON DELETE CASCADE NOT NULL);

CREATE TABLE IF NOT EXISTS USERS (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name varchar,
                                     login varchar NOT NULL,
                                     email varchar NOT NULL,
                                     birthday date NOT NULL);

CREATE TABLE IF NOT EXISTS FRIENDS (
                                      id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                      user_id integer REFERENCES users (id) ON DELETE CASCADE NOT NULL,
                                      friend_id integer REFERENCES users (id) ON DELETE CASCADE NOT NULL);


CREATE TABLE IF NOT EXISTS FILM_LIKES (
                                              id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                              film_id integer REFERENCES films (id) ON DELETE CASCADE NOT NULL,
                                              user_id integer REFERENCES users (id) ON DELETE CASCADE NOT NULL);
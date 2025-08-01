CREATE TABLE IF NOT EXISTS films (
              id INTEGER,
              description varchar(210),
              name varchar(100) NOT NULL,
              duration integer,
              releaseDate date,
              mpa_id integer,
              CONSTRAINT films_pk PRIMARY KEY (id)
          );
CREATE TABLE IF NOT EXISTS users (
              id INTEGER,
              email varchar(100) NOT NULL,
              name varchar(200),
              login varchar(200) NOT NULL,
              birthday date,
              CONSTRAINT users_pk PRIMARY KEY (id)
          );
CREATE TABLE IF NOT EXISTS friends (
                        user_id INTEGER NOT NULL,
                        friend_id INTEGER NOT NULL,
                        CONSTRAINT friends_pk PRIMARY KEY (user_id, friend_id)
                    );
CREATE TABLE IF NOT EXISTS likes (
                        film_id INTEGER NOT NULL,
                        user_id INTEGER NOT NULL,
                        CONSTRAINT likes_pk PRIMARY KEY (film_id, user_id)
                    );
CREATE TABLE IF NOT EXISTS film_genres (
                        film_id INTEGER NOT NULL,
                        genre_id INTEGER NOT NULL,
                        CONSTRAINT film_genres_pk PRIMARY KEY (film_id, genre_id)
                    );
CREATE TABLE IF NOT EXISTS genre (
    id INTEGER NOT NULL,
    name varchar(40) NOT NULL,
    CONSTRAINT genre_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS mpa (
    id INTEGER NOT NULL,
    name varchar(40) NOT NULL,
    CONSTRAINT mpa_pk PRIMARY KEY (id)
);
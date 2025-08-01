package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.rowMapper.FilmGenresRowMapper;
import ru.yandex.practicum.filmorate.rowMapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import java.util.*;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("genreDbStorage") GenreStorage genreStorage, @Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", new FilmRowMapper());
        films.forEach(film -> {
            getLikes(film);
            getGenres(film);
            int mpaId = 0;
            if (Optional.ofNullable(film.getMpa()).isPresent()) {
                mpaId = film.getMpa().getId();
                film.setMpa(mpaStorage.findById(mpaId));
            }
        });
        return films;
    }

    @Override
    public Film create(Film film) {
        log.debug("create film start");
        jdbcTemplate.update(
                "INSERT INTO films VALUES (?, ?, ?, ?, ?,?)",
                film.getId(), film.getDescription(), film.getName(),
                film.getDuration(), film.getReleaseDate(), film.getMpa().getId());
        log.debug("create film");
        for (Genre genre:film.getGenres()) {

            jdbcTemplate.update(
                    "INSERT INTO film_genres VALUES (?, ?)",
                    film.getId(), genre.getId());
            log.debug("create genre");
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        Optional.ofNullable(film).orElseThrow(() ->
                new NotFoundException("Фильм пустой"));
        int mpaId = 0;
        if (Optional.ofNullable(film.getMpa()).isPresent()) {
            mpaId = film.getMpa().getId();
            film.setMpa(mpaStorage.findById(mpaId));
        }
        int status = jdbcTemplate.update("update films set name = ?, description = ?, duration = ?, releaseDate = ?, mpa_id = ? where id = ?",
                film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(), mpaId, film.getId());

        if (status == 0) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        return film;
    }

    @Override
    public int delete(long filmId) {
        return jdbcTemplate.update("delete from films where id = ?", filmId);
    }

    @Override
    public Film findById(long filmId) {
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM films WHERE id = ?", new Object[] { filmId }, Integer.class);
        if (count > 0) {
        Film film = Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM films where id = ?", new FilmRowMapper(), filmId))
                .orElseThrow(() ->
                new NotFoundException("Фильм с id = " + filmId + " не найден"));
        getLikes(film);
        getGenres(film);
        int mpaId = 0;
        if (Optional.ofNullable(film.getMpa()).isPresent()) {
            mpaId = film.getMpa().getId();
            film.setMpa(mpaStorage.findById(mpaId));
        }
        return film;
        } else {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    @Override
    public void addLike(Film film, long userId) {
        jdbcTemplate.update(
                "INSERT INTO likes VALUES (?, ?)",
                film.getId(), userId);
    }

    @Override
    public void deleteLike(Film film, long userId) {
        jdbcTemplate.update("delete from likes where film_id = ? and user_id = ?", film.getId(), userId);
    }

    private void getLikes(Film film) {
        int likesCount = jdbcTemplate.queryForObject("SELECT count(*) FROM likes WHERE film_id = ?", new Object[] { film.getId() }, Integer.class);
        if (likesCount > 0) {
            for (Long userId:jdbcTemplate.queryForList("SELECT user_id FROM likes where film_id = ?", Long.class, film.getId())) {
                film.addLike(userId);
            }
        }
    }

    private void getGenres(Film film) {
        int genresCount = jdbcTemplate.queryForObject("SELECT count(*) FROM film_genres WHERE film_id = ?", new Object[] { film.getId() }, Integer.class);
        if (genresCount > 0) {
            jdbcTemplate.query("SELECT genre_id FROM film_genres where film_id = ?", new FilmGenresRowMapper(), film.getId()).forEach(genre -> {
                film.addGenre(genreStorage.findById(genre.getId()));
            });
        }
    }
}
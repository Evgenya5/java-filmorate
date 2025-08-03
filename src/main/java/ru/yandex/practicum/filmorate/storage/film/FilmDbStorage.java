package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.rowMapper.FilmRowMapper;
import java.util.*;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        Map<Long, Film> films = new HashMap<>();
        List<Film> filmList = jdbcTemplate.query("SELECT f.*, m.name as mpa_name, l.user_id, g.id as genre_id, g.name as genre_name FROM films f " +
                "left join mpa m on m.id = f.mpa_id " +
                "left join likes l on l.film_id = f.id  " +
                "left join film_genres fg on fg.film_id = f.id " +
                "left Join genre g on g.id = fg.genre_id ", new FilmRowMapper());
        filmList.forEach(film1 -> {
            if (films.containsKey(film1.getId())) {
                Film film = films.get(film1.getId());
                if (!film1.getLikes().isEmpty() && !film.getLikes().contains(film1.getLikes().stream().findFirst().get())) {
                    film.addLike(film1.getLikes().stream().findFirst().get());
                }
                if (!film1.getGenres().isEmpty() && !film.getGenres().contains(film1.getGenres().stream().findFirst().get())) {
                    film.addGenre(film1.getGenres().stream().findFirst().get());
                }
                films.put(film.getId(), film);
            } else {
                films.put(film1.getId(), film1);
            }
        });
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.debug("create film start");
        jdbcTemplate.update(
                "INSERT INTO films VALUES (?, ?, ?, ?, ?,?)",
                film.getId(), film.getDescription(), film.getName(),
                film.getDuration(), film.getReleaseDate(), film.getMpa().getId());
        log.debug("create film");
        addFilmGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Optional.ofNullable(film).orElseThrow(() ->
                new NotFoundException("Фильм пустой"));
        int status = jdbcTemplate.update("update films set name = ?, description = ?, duration = ?, releaseDate = ?, mpa_id = ? where id = ?",
                film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(), film.getMpa().getId(), film.getId());

        if (status == 0) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        jdbcTemplate.update("delete from film_genres where film_id = ?", film.getId());
        addFilmGenres(film);
        return film;
    }

    @Override
    public int delete(long filmId) {
        return jdbcTemplate.update("delete from films where id = ?", filmId);
    }

    @Override
    public Film findById(long filmId) {
        try {
            List<Film> filmList = jdbcTemplate.query("SELECT f.*, m.name as mpa_name, l.user_id, g.id as genre_id, g.name as genre_name FROM films f " +
                    "left join mpa m on m.id = f.mpa_id " +
                    "left join likes l on l.film_id = f.id  " +
                    "left join film_genres fg on fg.film_id = f.id " +
                    "left Join genre g on g.id = fg.genre_id " +
                    "where f.id = ?", new FilmRowMapper(), filmId);
            if (filmList.isEmpty()) {
                throw new NotFoundException("Фильм с id = " + filmId + " не найден");
            }
            Film film = filmList.getFirst();
            filmList.removeFirst();
            filmList.forEach(film1 -> {
                if (!film1.getLikes().isEmpty() && !film.getLikes().contains(film1.getLikes().stream().findFirst().get())) {
                    film.addLike(film1.getLikes().stream().findFirst().get());
                }
                if (!film1.getGenres().isEmpty() && !film.getGenres().contains(film1.getGenres().stream().findFirst().get())) {
                    film.addGenre(film1.getGenres().stream().findFirst().get());
                }
            });
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id = " + filmId + " не найден");
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

    private void addFilmGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    "INSERT INTO film_genres VALUES (?, ?)",
                    film.getId(), genre.getId());
        }
    }
}
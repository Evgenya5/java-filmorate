package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.rowMapper.GenreRowMapper;

import java.util.*;

@Component
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final Map<Integer, Genre> genres = new HashMap<>();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        jdbcTemplate.query("SELECT id, name FROM genre", new GenreRowMapper())
                .forEach(genre -> {
                    genres.put(genre.getId(),genre);
                });
        return genres.values();
    }

    @Override
    public Genre findById(int id) {
        if (genres.isEmpty()) {
            findAll();
        }
        genres.get(id);
        return Optional.ofNullable(genres.get(id))
                .orElseThrow(() ->
                        new NotFoundException("Жанр с id = " + id + " не найден"));
    }
}
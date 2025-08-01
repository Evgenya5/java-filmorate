package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.rowMapper.GenreRowMapper;
import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM genre", new GenreRowMapper());
    }

    @Override
    public Genre findById(int id) {
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM genre WHERE id = ?", new Object[] { id }, Integer.class);
        if (count > 0) {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT id, name FROM genre where id = ?", new GenreRowMapper(), id))
                .orElseThrow(() ->
                new NotFoundException("Жанр с id = " + id + " не найден"));
        } else {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
    }
}
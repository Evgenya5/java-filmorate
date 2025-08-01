package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.rowMapper.MpaRowMapper;
import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Collection<Mpa> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM mpa", new MpaRowMapper());
    }

    @Override
    public Mpa findById(int id) {
        if (id == 0) {
           return new Mpa();
        }
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM mpa WHERE id = ?", new Object[] { id }, Integer.class);
        if (count > 0) {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT id, name FROM mpa where id = ?", new MpaRowMapper(), id))
                .orElseThrow(() ->
                new NotFoundException("Рейтинг с id = " + id + " не найден"));
        } else {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден");
        }
    }
}
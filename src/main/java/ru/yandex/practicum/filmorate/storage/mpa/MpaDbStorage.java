package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.rowMapper.MpaRowMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final Map<Integer, Mpa> mpas = new HashMap<>();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {
        jdbcTemplate.query("SELECT id, name FROM mpa", new MpaRowMapper()).forEach(mpa -> {
                    mpas.put(mpa.getId(), mpa);
                }
        );
        return mpas.values();
    }

    @Override
    public Mpa findById(int id) {
        if (id == 0) {
            return new Mpa();
        }
        if (mpas.isEmpty()) {
            findAll();
        }
        return Optional.ofNullable(mpas.get(id))
                .orElseThrow(() ->
                        new NotFoundException("Рейтинг с id = " + id + " не найден"));
    }
}
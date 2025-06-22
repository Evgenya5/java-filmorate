package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        return films.put(film.getId(), film);
    }

    @Override
    public Film update(Film film) {
        return films.replace(film.getId(), film);
    }

    @Override
    public void delete(long filmId) {
        films.remove(filmId);
    }

    @Override
    public Film findById(long filmId) {
        return films.get(filmId);
    }
}
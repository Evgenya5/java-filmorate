package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        Optional.ofNullable(film).orElseThrow(() ->
                new NotFoundException("Фильм пустой"));
        Optional.ofNullable(films.get(film.getId())).orElseThrow(() ->
                new NotFoundException("Фильм с id " + film.getId() + " не найден"));
        return films.replace(film.getId(), film);
    }

    @Override
    public void delete(long filmId) {
        films.remove(filmId);
    }

    @Override
    public Film findById(long filmId) {
        return Optional.ofNullable(films.get(filmId)).orElseThrow(() ->
                new NotFoundException("Фильм с id = " + filmId + " не найден"));
    }
}
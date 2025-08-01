package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    int delete(long filmId);

    Film findById(long filmId);

    void addLike(Film film, long userId);

    void deleteLike(Film film, long userId);
}
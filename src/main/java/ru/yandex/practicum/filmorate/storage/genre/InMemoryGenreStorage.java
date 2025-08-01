package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("inMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Integer, Genre> genres = new HashMap<>();

    @Override
    public Collection<Genre> findAll() {
        return genres.values();
    }

    @Override
    public Genre findById(int id) {
        return Optional.ofNullable(genres.get(id)).orElseThrow(() ->
                new NotFoundException("Жанр с id = " + id + " не найден"));
    }
}
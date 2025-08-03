package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Table(name = "films")
public class Film {
    @Id
    private long id;
    private String description;
    private String name;
    private LocalDate releaseDate;
    private int duration;
    private Mpa mpa;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();

    public void addLike(long userId) {
        likes.add(userId);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void deleteLike(long userId) {
        likes.remove(userId);
    }
}
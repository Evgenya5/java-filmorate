package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String description;
    private String name;
    private LocalDate releaseDate;
    private int duration;
}

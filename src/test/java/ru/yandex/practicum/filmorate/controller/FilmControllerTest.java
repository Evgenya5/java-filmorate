package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

class FilmControllerTest {
    private static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    FilmController filmController;
    Film film1;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
        film1 = new Film();
        film1.setDuration(100);
        film1.setReleaseDate(FIRST_RELEASE_DATE.plusDays(100));
        film1.setDescription("desc");
        film1.setName("name");
    }

    @Test
    void findAll() {
        Collection<Film> films = new HashSet<>();
        Film film = filmController.create(film1);
        films.add(film);
        film1.setName("name2");
        film = filmController.create(film1);
        films.add(film);
        Assertions.assertEquals(filmController.findAll().stream().toList(), films.stream().toList());
    }

    @Test
    void create() {
        Film film = new Film();

        ValidationException exc = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        Assertions.assertEquals("название не может быть пустым", exc.getMessage());
        film.setName("testName");
        Film createFilm = filmController.create(film);
        Assertions.assertEquals(film.getName(), createFilm.getName());
        Assertions.assertNotNull(createFilm.getId());
        film.setDescription("очень длинный текст описания, больше 200 символов, очень длинный текст описания, больше 200 символов, " +
                "очень длинный текст описания, больше 200 символов, очень длинный текст описания, больше 200 символов, очень длинный текст описания, больше 200 символов");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        Assertions.assertEquals("максимальная длина описания — 200 символов", exc.getMessage());
        film.setDescription("desc");
        createFilm = filmController.create(film);
        Assertions.assertEquals(film.getDescription(), createFilm.getDescription());
        Assertions.assertNotNull(createFilm.getId());
        film.setReleaseDate(FIRST_RELEASE_DATE.minusDays(10));
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        Assertions.assertEquals("дата релиза — не раньше 28 декабря 1895 года", exc.getMessage());
        film.setReleaseDate(FIRST_RELEASE_DATE.plusDays(10));
        createFilm = filmController.create(film);
        Assertions.assertEquals(film.getReleaseDate(), createFilm.getReleaseDate());
        Assertions.assertNotNull(createFilm.getId());
        film.setDuration(-50);
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        Assertions.assertEquals("продолжительность фильма должна быть положительным числом", exc.getMessage());
        film.setDuration(100);
        createFilm = filmController.create(film);
        Assertions.assertEquals(film.getDuration(), createFilm.getDuration());
        Assertions.assertNotNull(createFilm.getId());
    }

    @Test
    void update() {
        Film film = filmController.create(film1);
        film.setName("");
        ValidationException exc = Assertions.assertThrows(ValidationException.class,() -> {
            filmController.update(film);
        });
        Assertions.assertEquals("название не может быть пустым", exc.getMessage());
        film.setName("testName");
        Film updateFilm = filmController.update(film);
        Assertions.assertEquals(film.getName(), updateFilm.getName());
        Assertions.assertEquals(film1.getId(), updateFilm.getId());
        film.setDescription("очень длинный текст описания, больше 200 символов, очень длинный текст описания, больше 200 символов, " +
                "очень длинный текст описания, больше 200 символов, очень длинный текст описания, больше 200 символов, очень длинный текст описания, больше 200 символов");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.update(film);
        });
        Assertions.assertEquals("максимальная длина описания — 200 символов", exc.getMessage());
        film.setDescription("desc");
        updateFilm = filmController.update(film);
        Assertions.assertEquals(film.getDescription(), updateFilm.getDescription());
        Assertions.assertEquals(film1.getId(), updateFilm.getId());
        film.setReleaseDate(FIRST_RELEASE_DATE.minusDays(10));
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.update(film);
        });
        Assertions.assertEquals("дата релиза — не раньше 28 декабря 1895 года", exc.getMessage());
        film.setReleaseDate(FIRST_RELEASE_DATE.plusDays(10));
        updateFilm = filmController.update(film);
        Assertions.assertEquals(film.getReleaseDate(), updateFilm.getReleaseDate());
        Assertions.assertEquals(film1.getId(), updateFilm.getId());
        film.setDuration(-50);
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.update(film);
        });
        Assertions.assertEquals("продолжительность фильма должна быть положительным числом", exc.getMessage());
        film.setDuration(100);
        updateFilm = filmController.update(film);
        Assertions.assertEquals(film.getDuration(), updateFilm.getDuration());
        Assertions.assertEquals(film1.getId(), updateFilm.getId());
        film.setId(null);
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.update(film);
        });
        Assertions.assertEquals("Id должен быть указан", exc.getMessage());
    }
}
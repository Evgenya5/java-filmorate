package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    @Autowired
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") long id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopularFilms(@RequestParam(required = false) int count) {
        return filmService.findPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long userId, @PathVariable long id) {
        filmService.deleteLike(id, userId);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug(film.getDescription());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
            return filmService.update(film);
    }
}
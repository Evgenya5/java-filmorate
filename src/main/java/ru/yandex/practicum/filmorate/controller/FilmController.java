package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final static LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        // проверяем выполнение необходимых условий
        validateFilm(film);
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новую запись в памяти приложения
        films.put(film.getId(), film);
        log.debug("create film with id " + film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {

        if (film.getId() == null) {
            log.error("update error: id is null");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            // если найдена и все условия соблюдены, обновляем её содержимое
            if (film.getName() != null) {
                if (film.getName().isBlank()) {
                    log.error("empty new film name");
                    throw new ValidationException("название не может быть пустым");
                } else {
                    oldFilm.setName(film.getName());
                }
            }
            if (film.getDescription() != null) {
                if (film.getDescription().length() > 200) {
                    log.error("long film new description");
                    throw new ValidationException("максимальная длина описания — 200 символов");
                } else {
                    oldFilm.setDescription(film.getDescription());
                }
            }
            if (film.getReleaseDate() != null) {
               if (film.getReleaseDate().isBefore(FIRST_RELEASE_DATE)) {
                   log.error("new release date is before {}", FIRST_RELEASE_DATE);
                   throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
               } else {
                   oldFilm.setReleaseDate(film.getReleaseDate());
               }
            }
            if (film.getDuration() > 0) {
                oldFilm.setDuration(film.getDuration());
            } else {
                log.error("new film duration < 0");
                throw new ValidationException("продолжительность фильма должна быть положительным числом");
            }
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("empty film name");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("long film description");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_RELEASE_DATE)) {
            log.error("release date is before {}", FIRST_RELEASE_DATE);
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("film duration < 0");
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
    }
}

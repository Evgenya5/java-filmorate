package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmService {

    private static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int DEFAULT_COUNT = 10;
    FilmStorage filmStorage;
    UserService userService;

    @Autowired
    public FilmService(UserService userService, FilmStorage filmStorage) {
        this.userService = userService;
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(long filmId) {
        Film film = filmStorage.findById(filmId);
        if (film != null) {
            return film;
        }
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    public void addLike(long filmId, long userId) {
        log.error("add like filmID " + filmId);
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        log.error("add like userID " + userId);
        User user = userService.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        log.debug("добавлен лайк");
        film.addLike(userId);
        log.debug(film.getLikes().toString());
    }

    public void deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        User user = userService.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        film.deleteLike(userId);
    }

    public Collection<Film> findPopularFilms(int count) {
        Comparator<Film> getMaxLikes = (s1, s2) -> s2.getLikes().size() - s1.getLikes().size();

        if (count <=0) {
            count = DEFAULT_COUNT;
        }
        return filmStorage.findAll().stream()
                .sorted(getMaxLikes)
                .limit(count)
                .toList();
    }

    public Film create(Film film) {
        // проверяем выполнение необходимых условий
        log.debug(film.getDescription());
        validateFilm(film);
        // формируем дополнительные данные
        film.setId(getNextId());
        filmStorage.create(film);
        // сохраняем новую запись в памяти приложения
        log.debug("create film with id " + film.getId());
        return film;
    }

    public Film update(Film film) {

        if (film.getId() == null) {
            log.error("update error: id is null");
            throw new ValidationException("Id должен быть указан");
        }
        if (filmStorage.findById(film.getId()) != null) {
            Film oldFilm = filmStorage.findById(film.getId());
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
            return filmStorage.update(oldFilm);
        }
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = filmStorage.findAll()
                .stream()
                .mapToLong(Film::getId)
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
package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        validateUser(user);
        // формируем дополнительные данные
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("create user with id: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            log.error("user id empty");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            // если найдена и все условия соблюдены, обновляем её содержимое
            if (user.getLogin() != null) {
                if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                    log.error("not valid login");
                    throw new ValidationException("логин не может быть пустым и содержать пробелы");
                } else {
                    if (oldUser.getLogin().equals(oldUser.getName())) { //если имя было приравнено к логину, обновляем и его
                        oldUser.setName(user.getLogin());
                    }
                    oldUser.setLogin(user.getLogin());
                }
            }
            if (user.getEmail() != null) {
                if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                    log.error("not valid email");
                    throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
                } else {
                    oldUser.setEmail(user.getEmail());
                }
            }
            if (user.getName() != null && !user.getName().isBlank()) {
                oldUser.setName(user.getName());
            }

            if (user.getName() != null && user.getName().isBlank()) {
                oldUser.setName(oldUser.getLogin());
            }

            if (user.getBirthday() != null) {
                if (user.getBirthday().isAfter(LocalDate.now())) {
                    log.error("update birthday is future");
                    throw new ValidationException("дата рождения не может быть в будущем");
                } else {
                    oldUser.setBirthday(user.getBirthday());
                }
            }
            log.debug("update user with id: {}", oldUser.getId());
            return oldUser;
        }
        log.error("user not found. Id {}", user.getId());
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("empty login");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("empty email");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("empty name, set name = login = {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("birthday is future");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
    }
}

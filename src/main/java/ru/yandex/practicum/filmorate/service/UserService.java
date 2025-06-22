package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage = new InMemoryUserStorage();

    public User findById(long userId) {
        User user = userStorage.findById(userId);
        if (user != null) {
            return user;
        }
        throw new NotFoundException("Пользователь с id = " + userId + " не найден");
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        // проверяем выполнение необходимых условий
        validateUser(user);
        // формируем дополнительные данные
        user.setId(getNextId());
        userStorage.create(user);
        log.debug("create user with id: {}", user.getId());
        return user;
    }

    public User update(User user) {
        if (user.getId() == null) {
            log.error("user id empty");
            throw new ValidationException("Id должен быть указан");
        }
        if (userStorage.findById(user.getId()) != null) {
            User oldUser = userStorage.findById(user.getId());
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
            userStorage.update(oldUser);
            return oldUser;
        }
        log.error("user not found. Id {}", user.getId());
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        User friendUser = userStorage.findById(friendId);
        if (friendUser == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        user.addFriend(friendUser.getId());
        friendUser.addFriend(user.getId());
    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        user.deleteFriend(friendId);
        User friendUser = userStorage.findById(friendId);
        if (friendUser == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        friendUser.deleteFriend(userId);
    }

    public Collection<User> getFriends(long id) {
        Collection<User> friends = new ArrayList<>();
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        for (long friendId:user.getFriends()){
            friends.add(userStorage.findById(friendId));
        }
        return friends;
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        Collection<User> friends = new ArrayList<>();
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        User otherUser = userStorage.findById(otherId);
        if (otherUser == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        for (long friendId:user.getFriends()){
            if (otherUser.getFriends().contains(friendId)) {
                friends.add(userStorage.findById(friendId));
            }
        }
        return friends;
    }

    private long getNextId() {
        long currentMaxId = userStorage.findAll()
                .stream()
                .mapToLong(User::getId)
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

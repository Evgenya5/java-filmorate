package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.rowMapper.UserRowMapper;

import java.util.*;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User findById(long id) {
        try {
            List<User> userList = jdbcTemplate.query("SELECT u.*, f.friend_id as friend_id FROM users u left join friends f on f.user_id = u.id where u.id = ?", new UserRowMapper(), id);
            if (userList.isEmpty()) {
                throw new NotFoundException("Пользователь с id = " + id + " не найден");
            }
            User user = userList.getFirst();
            userList.removeFirst();
            userList.forEach(user1 -> {
                user.addFriend(user1.getFriends().stream().findFirst().get());
            });
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public Collection<User> findAll() {
        Map<Long, User> users = new HashMap<>();
        List<User> userList = jdbcTemplate.query("SELECT u.*, f.friend_id as friend_id FROM users u left join friends f on f.user_id = u.id", new UserRowMapper());
        userList.forEach(user -> {
            if (users.containsKey(user.getId())) {
                users.get(user.getId()).addFriend(user.getFriends().stream().findFirst().get());
            } else {
                users.put(user.getId(), user);
            }
        });
        return users.values();
    }

    @Override
    public User create(User user) {
        jdbcTemplate.update(
                "INSERT INTO users VALUES (?, ?, ?, ?, ?)",
                user.getId(), user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday());
        return user;
    }

    @Override
    public User update(User user) {
        Optional.ofNullable(user).orElseThrow(() ->
                new NotFoundException("Пользователь пустой"));
        int status = jdbcTemplate.update("update users set name = ?, login = ?, email = ?, birthday = ? where id = ?",
                user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());

        if (status == 0) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        return user;
    }

    @Override
    public void addFriend(User user, User friend) {
        jdbcTemplate.update(
                "INSERT INTO friends VALUES (?, ?)",
                user.getId(), friend.getId());
    }

    @Override
    public void deleteFriend(User user, User friendUser) {
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", user.getId(), friendUser.getId());
    }
}
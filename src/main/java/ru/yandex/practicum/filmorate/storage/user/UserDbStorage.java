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
            return jdbcTemplate.queryForObject("SELECT * FROM users where id = ?", new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", new UserRowMapper());
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

    @Override
    public Collection<User> getCommonFriends(User user, User otherUser) {
        try {
            return jdbcTemplate.query("SELECT u.* FROM friends f join users u ON f.friend_id = u.id where f.user_id in (?, ?) group by u.id having count(u.id) = 2", new UserRowMapper(), user.getId(), otherUser.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Друзья для пользователей с id = " + user.getId() + ", " + otherUser.getId());
        }
    }

    @Override
    public Collection<User> getFriends(User user) {
        try {
            return jdbcTemplate.query("SELECT u.* FROM friends f join users u ON f.friend_id = u.id where f.user_id = ?", new UserRowMapper(), user.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Друзья для пользователя с id = " + user.getId());
        }
    }
}
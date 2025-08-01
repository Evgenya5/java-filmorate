package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.rowMapper.UserRowMapper;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM users WHERE id = ?", new Object[] { id }, Integer.class);
        if (count > 0) {
            User user = Optional.ofNullable(jdbcTemplate.queryForObject("SELECT id, email, login, name, birthday FROM users where id = ?", new UserRowMapper(), id))
                    .orElseThrow(() ->
                            new NotFoundException("Пользователь с id = " + id + " не найден"));
            getFriends(user);
            return user;
        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public Collection<User> findAll() {
        List<User> users = jdbcTemplate.query("SELECT id, email, login, name, birthday FROM users", new UserRowMapper());
        users.forEach(this::getFriends);
        return users;
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

    private void getFriends(User user) {
        int friendCount = jdbcTemplate.queryForObject("SELECT count(*) FROM friends WHERE user_id = ?", new Object[] { user.getId() }, Integer.class);
        if (friendCount > 0) {
            for (Long friendId:jdbcTemplate.queryForList("SELECT friend_id FROM friends where user_id = ?", Long.class, user.getId())) {
                user.addFriend(friendId);
            }
        }
    }
}
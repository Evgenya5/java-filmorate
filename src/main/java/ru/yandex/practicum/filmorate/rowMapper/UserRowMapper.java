package ru.yandex.practicum.filmorate.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getObject("id", Long.class));
        user.setEmail(resultSet.getObject("email", String.class));
        user.setLogin(resultSet.getObject("login", String.class));
        user.setName(resultSet.getObject("name", String.class));
        user.setBirthday(resultSet.getObject("birthday", LocalDate.class));
        if (Optional.ofNullable(resultSet.getObject("friend_id", Long.class)).isPresent()) {
            user.addFriend(resultSet.getObject("friend_id", Long.class));
        }
        return user;
    }
}
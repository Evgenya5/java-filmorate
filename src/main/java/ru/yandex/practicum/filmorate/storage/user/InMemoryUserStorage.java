package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User findById(long id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public User update(User user) {
        Optional.ofNullable(user).orElseThrow(() ->
                new NotFoundException("Пользователь пустой"));
        Optional.ofNullable(users.get(user.getId())).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + user.getId() + " не найден"));
        return users.replace(user.getId(), user);
    }

    @Override
    public void addFriend(User user, User friend) {
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
    }

    @Override
    public void deleteFriend(User user, User friendUser) {
        user.deleteFriend(friendUser.getId());
        friendUser.deleteFriend(user.getId());
    }
}
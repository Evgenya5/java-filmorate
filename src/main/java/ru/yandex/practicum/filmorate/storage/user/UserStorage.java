package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {

    User findById(long id);

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friendUser);

    Collection<User> getCommonFriends(User user, User otherUser);

    Collection<User> getFriends(User user);
}
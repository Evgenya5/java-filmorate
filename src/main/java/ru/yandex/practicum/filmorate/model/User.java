package ru.yandex.practicum.filmorate.model;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User.
 */
@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public void addFriend(long userId) {
        friends.add(userId);
    }

    public void deleteFriend(long userId) {
        friends.remove(userId);
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

class UserControllerTest {

    UserController userController;
    User user1;
    UserService userService;

    @BeforeEach
    void beforeEach() {
        userService = new UserService();
        userController = new UserController(userService);
        user1 = new User();
        user1.setName("name");
        user1.setBirthday(LocalDate.of(1980,1,1));
        user1.setEmail("a@a.ru");
        user1.setLogin("login");
    }

    @Test
    void findAll() {
        Collection<User> users = new HashSet<>();
        User user = userController.create(user1);
        users.add(user);
        user1.setName("name2");
        user = userController.create(user1);
        users.add(user);
        Assertions.assertEquals(userController.findAll().stream().toList(), users.stream().toList());
    }

    @Test
    void create() {
        User user = new User();
        ValidationException exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        Assertions.assertEquals("логин не может быть пустым и содержать пробелы", exc.getMessage());
        user.setLogin("df df");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        Assertions.assertEquals("логин не может быть пустым и содержать пробелы", exc.getMessage());
        user.setLogin("login");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        Assertions.assertEquals("электронная почта не может быть пустой и должна содержать символ @", exc.getMessage());
        user.setEmail("email");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        Assertions.assertEquals("электронная почта не может быть пустой и должна содержать символ @", exc.getMessage());
        user.setEmail("email@mail.ru");
        User createUser = userController.create(user);
        Assertions.assertEquals(createUser.getLogin(), user.getLogin());
        Assertions.assertEquals(createUser.getEmail(), user.getEmail());
        Assertions.assertEquals(createUser.getLogin(), createUser.getName());
        Assertions.assertNotNull(createUser.getId());
        user.setBirthday(LocalDate.now().plusDays(10));
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        Assertions.assertEquals("дата рождения не может быть в будущем", exc.getMessage());
        user.setBirthday(LocalDate.now().minusYears(10));
        createUser = userController.create(user);
        Assertions.assertEquals(createUser.getBirthday(), user.getBirthday());
        Assertions.assertNotNull(createUser.getId());
        user.setName("name");
        createUser = userController.create(user);
        Assertions.assertEquals(createUser.getName(), user.getName());
        Assertions.assertNotNull(createUser.getId());
    }

    @Test
    void update() {
        User user = userController.create(user1);
        ValidationException exc;
        user.setName("");
        User updateUser = userController.update(user);
        Assertions.assertEquals(updateUser.getId(), user.getId());
        Assertions.assertEquals(updateUser.getName(), updateUser.getLogin());
        user.setName("newName");
        updateUser = userController.update(user);
        Assertions.assertEquals(updateUser.getId(), user.getId());
        Assertions.assertEquals(updateUser.getName(), user.getName());

        user.setLogin("");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });
        Assertions.assertEquals("логин не может быть пустым и содержать пробелы", exc.getMessage());
        user.setLogin("df df");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });
        Assertions.assertEquals("логин не может быть пустым и содержать пробелы", exc.getMessage());
        user.setLogin("login");
        updateUser = userController.update(user);
        Assertions.assertEquals(updateUser.getLogin(), user.getLogin());
        user.setEmail("");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });
        Assertions.assertEquals("электронная почта не может быть пустой и должна содержать символ @", exc.getMessage());
        user.setEmail("email");
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });
        Assertions.assertEquals("электронная почта не может быть пустой и должна содержать символ @", exc.getMessage());
        user.setEmail("email@mail.ru");
        updateUser = userController.update(user);
        Assertions.assertEquals(updateUser.getEmail(), user.getEmail());
        Assertions.assertEquals(updateUser.getId(), user.getId());
        user.setBirthday(LocalDate.now().plusDays(10));
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });
        Assertions.assertEquals("дата рождения не может быть в будущем", exc.getMessage());
        user.setBirthday(LocalDate.now().minusYears(10));
        updateUser = userController.update(user);
        Assertions.assertEquals(updateUser.getBirthday(), user.getBirthday());
        Assertions.assertEquals(updateUser.getId(), user.getId());
        user.setId(null);
        exc = Assertions.assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });
        Assertions.assertEquals("Id должен быть указан", exc.getMessage());
    }
}
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        User oldUser = users.get(user.getId());
        if (user.getBirthday().isBefore(LocalDate.now()) && user.getBirthday() != null) {
            oldUser.setBirthday(user.getBirthday());
        }
        if (!user.getLogin().isBlank()) {
            oldUser.setLogin(user.getLogin());
        }
        if (!user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail().contains("@") && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }

        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void validate(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("Неправильный email");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин должен быть не пустой и не может содержть пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения должна быть раньше настоящего времени");
        }

    }
}
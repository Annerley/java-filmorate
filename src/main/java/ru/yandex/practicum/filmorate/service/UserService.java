package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public void addFriend(Long id, Long friendId) {

        if (Objects.equals(id, friendId)) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден"));
        User friend = storage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + friendId + " не найден"));

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        storage.updateUser(user);
        storage.updateUser(friend);

    }

    public void deleteFriend(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            throw new ValidationException("Нельзя удалить себя из друзей");
        }
        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден"));
        User friend = storage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + friendId + " не найден"));
        if (!user.getFriends().contains(friend.getId())) return;
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
        storage.updateUser(user);
        storage.updateUser(friend);
    }

    public List<User> showAllFriends(Long id) {
        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден"));
        Set<Long> ids = user.getFriends() == null ? Set.of() : user.getFriends();

        return ids.stream()
                .map(fid -> storage.findById(fid)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Пользователь " + fid + " не найден")))
                .toList();
    }

    public List<User> commonFriends(Long userId, Long friendsId) {
        User user = storage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        User friend = storage.findById(friendsId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + friendsId + " не найден"));

        Set<Long> mutualIds = new HashSet<>(user.getFriends());
        mutualIds.retainAll(friend.getFriends()); // пересечение


        return mutualIds.stream()
                .map(id -> storage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден")))
                .toList();
    }
}
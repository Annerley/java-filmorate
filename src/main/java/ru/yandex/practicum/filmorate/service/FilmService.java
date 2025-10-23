package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

    public void addLike(Long filmId, Long userId) {
        Film film = storage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм " + filmId + " не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        if (film.getLikes().contains(user.getId())) throw new ValidationException("Вы уже поставили лайк этому фильму");
        film.getLikes().add(user.getId());
        storage.updateFilm(film);
    }

    public void deleteLike(Long filmId, Long userId) {

        Film film = storage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм " + filmId + " не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        if (!film.getLikes().contains(user.getId())) throw new ValidationException("Вы не ставили лайк этому фильму");
        film.getLikes().remove(user.getId());
        storage.updateFilm(film);
    }

    public List<Film> showTenMostPopular(Long count) {
        return storage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}
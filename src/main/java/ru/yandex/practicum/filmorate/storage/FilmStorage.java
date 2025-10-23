package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> findById(Long id);

    Collection<Film> findAll();

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public void deleteFilm(Film film);

}
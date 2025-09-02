package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

import java.util.Map;



@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate FIRST_FILM = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        Film oldFilm = films.get(film.getId());
        if (!(film.getDescription() != null && film.getDescription().length() > 200)) {

            oldFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate().isAfter(FIRST_FILM)) {
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        if (!film.getName().isBlank()) {
            oldFilm.setName(film.getName());
        }
        if (film.getDuration() > 0) {

            oldFilm.setDuration(film.getDuration());
        }

        films.put(film.getId(), oldFilm);
        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void validate(Film film) {
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина оаписания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM)) {
            throw new ValidationException("Дата релиза должна быть позже 1895.12.28");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Длительность фильма должна быть больше нуля");
        }
    }

}
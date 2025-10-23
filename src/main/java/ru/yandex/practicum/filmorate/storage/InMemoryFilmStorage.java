package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate FIRST_FILM = LocalDate.of(1895, 12, 28);

    private long id = 0;

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(++id);
        validate(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Фильм с id=" + film.getId() + " не найден"
            );
        }

        if (film.getDescription() != null && film.getDescription().length() <= 200) {
            oldFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isAfter(FIRST_FILM)) {
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getName() != null && !film.getName().isBlank()) {
            oldFilm.setName(film.getName());
        }
        if (film.getDuration() > 0) {
            oldFilm.setDuration(film.getDuration());
        }

        films.put(oldFilm.getId(), oldFilm);
        return oldFilm;
    }

    @Override
    public void deleteFilm(Film film) {
        films.remove(film.getId());
    }

    public void validate(Film film) {
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM)) {
            throw new ValidationException("Дата релиза должна быть позже 1895.12.28");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Длительность фильма должна быть больше нуля");
        }
    }
}
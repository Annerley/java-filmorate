package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    long id;
    @NotNull
    String name;
    String description;
    @NotNull
    LocalDate releaseDate;
    @NotNull
    int duration;
}
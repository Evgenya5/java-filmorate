package ru.yandex.practicum.filmorate.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getObject("id", Long.class));
        film.setDescription(resultSet.getObject("description", String.class));
        film.setName(resultSet.getObject("name", String.class));
        film.setReleaseDate(resultSet.getObject("releaseDate", LocalDate.class));
        film.setDuration(resultSet.getObject("duration", Integer.class));
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getObject("mpa_id", Integer.class));
        mpa.setName(resultSet.getObject("mpa_name", String.class));
        film.setMpa(mpa);
        if (Optional.ofNullable(resultSet.getObject("user_id", Long.class)).isPresent()) {
            film.addLike(resultSet.getObject("user_id", Long.class));
        }
        Genre genre = new Genre();
        if (Optional.ofNullable(resultSet.getObject("genre_id", Integer.class)).isPresent()) {
            genre.setId(resultSet.getObject("genre_id", Integer.class));
        }
        if (Optional.ofNullable(resultSet.getObject("genre_name", String.class)).isPresent()) {
            genre.setName(resultSet.getObject("genre_name", String.class));
        }
        if (genre.getId() > 0) {
            film.addGenre(genre);
        }
        return film;
    }
}
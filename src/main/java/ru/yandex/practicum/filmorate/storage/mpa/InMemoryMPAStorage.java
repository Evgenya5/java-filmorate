package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("inMemoryMpaStorage")
public class InMemoryMPAStorage implements MpaStorage {
    private final Map<Integer, Mpa> mpas = new HashMap<>();

    @Override
    public Collection<Mpa> findAll() {
        return mpas.values();
    }

    @Override
    public Mpa findById(int id) {
        return mpas.get(id);
    }
}
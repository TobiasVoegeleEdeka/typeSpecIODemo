package com.example.demo.controller;

import com.example.demo.api.api.DirectionsApi;
import com.example.demo.api.model.Wegbeschreibung;
import com.example.demo.repository.WegbeschreibungRepository;
import com.example.demo.exception.SystemFault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class WegbeschreibungController implements DirectionsApi {

    private final WegbeschreibungRepository repository;

    public WegbeschreibungController(WegbeschreibungRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Wegbeschreibung> directionsList() {
        return repository.findAll();
    }

    @Override
    public Wegbeschreibung directionsCreate(Wegbeschreibung directions) {
        String id = directions.id() != null ? directions.id() : UUID.randomUUID().toString();
        Wegbeschreibung newDirections = new Wegbeschreibung(
                id,
                directions.startPoint(),
                directions.destination(),
                directions.person(),
                directions.estimatedMinutes());
        return repository.save(newDirections);
    }

    @Override
    public Wegbeschreibung directionsRead(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new SystemFault("Directions not found", "NOT_FOUND", 404));
    }

    @Override
    public void directionsDelete(String id) {
        if (!repository.findById(id).isPresent()) {
            throw new SystemFault("Directions not found", "NOT_FOUND", 404);
        }
        repository.delete(id);
    }
}

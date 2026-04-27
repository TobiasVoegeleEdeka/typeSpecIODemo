package com.example.demo.controller;

import com.example.demo.api.model.Wegbeschreibung;
import com.example.demo.repository.WegbeschreibungRepository;
import com.example.demo.exception.SystemFault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/directions")
@CrossOrigin(origins = "http://localhost:4200")
public class WegbeschreibungController {

    private final WegbeschreibungRepository repository;

    public WegbeschreibungController(WegbeschreibungRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Wegbeschreibung> list() {
        return repository.findAll();
    }

    @PostMapping
    public Wegbeschreibung create(@RequestBody Wegbeschreibung directions) {
        String id = directions.id() != null ? directions.id() : UUID.randomUUID().toString();
        Wegbeschreibung newDirections = new Wegbeschreibung(
                id,
                directions.startPoint(),
                directions.destination(),
                directions.person(),
                directions.estimatedMinutes()
        );
        return repository.save(newDirections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wegbeschreibung> read(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new SystemFault("Directions not found", "NOT_FOUND", 404));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repository.findById(id).isPresent()) {
            throw new SystemFault("Directions not found", "NOT_FOUND", 404);
        }
        repository.delete(id);
        return ResponseEntity.noContent().build();
    }
}

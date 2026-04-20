package com.example.demo.controller;

import com.example.demo.model.Wegbeschreibung;
import com.example.demo.repository.WegbeschreibungRepository;
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
                directions.Person(),
                directions.estimatedMinutes()
        );
        return repository.save(newDirections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wegbeschreibung> read(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (repository.findById(id).isPresent()) {
            repository.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

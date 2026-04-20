package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repository.InventoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@CrossOrigin(origins = "http://localhost:4200")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public List<Product> list() {
        return inventoryRepository.findAll();
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        String id = product.id() != null ? product.id() : UUID.randomUUID().toString();
        Product newProduct = new Product(id, product.name(), product.description(), product.price(), product.stock());
        return inventoryRepository.save(newProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> read(@PathVariable String id) {
        return inventoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable String id, @RequestBody Product productUpdate) {
        return inventoryRepository.findById(id).map(existing -> {
            Product updated = new Product(
                id,
                productUpdate.name() != null ? productUpdate.name() : existing.name(),
                productUpdate.description() != null ? productUpdate.description() : existing.description(),
                productUpdate.price() != 0 ? productUpdate.price() : existing.price(),
                productUpdate.stock() != 0 ? productUpdate.stock() : existing.stock()
            );
            inventoryRepository.save(updated);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (inventoryRepository.findById(id).isPresent()) {
            inventoryRepository.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

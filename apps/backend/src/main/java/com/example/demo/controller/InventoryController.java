package com.example.demo.controller;

import com.example.demo.api.api.InventoryApi;
import com.example.demo.api.model.Product;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.exception.SystemFault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class InventoryController implements InventoryApi {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<Product> inventoryList() {
        return inventoryRepository.findAll();
    }

    @Override
    public Product inventoryCreate(Product product) {
        String id = product.id() != null ? product.id() : UUID.randomUUID().toString();
        Product newProduct = new Product(id, product.name(), product.description(), product.price(), product.stock());
        return inventoryRepository.save(newProduct);
    }

    @Override
    public Product inventoryRead(String id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new SystemFault("Product not found", "NOT_FOUND", 404));
    }

    @Override
    public Product inventoryUpdate(String id, Product productUpdate) {
        return inventoryRepository.findById(id).map(existing -> {
            Product updated = new Product(
                    id,
                    productUpdate.name() != null ? productUpdate.name() : existing.name(),
                    productUpdate.description() != null ? productUpdate.description() : existing.description(),
                    productUpdate.price() != 0 ? productUpdate.price() : existing.price(),
                    productUpdate.stock() != 0 ? productUpdate.stock() : existing.stock());
            inventoryRepository.save(updated);
            return updated;
        }).orElseThrow(() -> new SystemFault("Product not found", "NOT_FOUND", 404));
    }

    @Override
    public void inventoryDelete(String id) {
        if (!inventoryRepository.findById(id).isPresent()) {
            throw new SystemFault("Product not found", "NOT_FOUND", 404);
        }
        inventoryRepository.delete(id);
    }
}

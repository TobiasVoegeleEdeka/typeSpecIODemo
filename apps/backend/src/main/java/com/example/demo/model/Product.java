package com.example.demo.model;

import java.util.UUID;

public record Product(
        String id,
        String name,
        String description,
        double price,
        int stock) {

    public static Product create(String name, String description, double price, int stock) {
        return new Product(UUID.randomUUID().toString(), name, description, price, stock);
    }
}

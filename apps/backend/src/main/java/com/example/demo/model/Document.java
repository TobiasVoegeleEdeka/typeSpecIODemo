package com.example.demo.model;

import java.time.LocalDateTime;

public record Document(
        int id,
        String name,
        String content,
        LocalDateTime createdAt) {

}

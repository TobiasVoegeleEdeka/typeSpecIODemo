package com.example.demo.model;

public record Task(
        int id,
        String title,
        String description,
        boolean completed) {

}

package com.example.demo.repository;

import com.example.demo.model.Document;
import com.example.demo.model.Product;
import com.example.demo.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;

@Service
public class MongoIndexInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MongoIndexInitializer.class);
    private final MongoTemplate mongoTemplate;

    public MongoIndexInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndices() {
        logger.info("Initializing MongoDB Indices (Manual/DOD Way)...");

        // Primary indexes on 'id' (mapped to _id) are automatic and unique by default.
        // We only add secondary indexes or non-standard indexes here.

        // Index for Tasks
        mongoTemplate.indexOps(Task.class).ensureIndex(
            new Index().on("title", Sort.Direction.ASC)
        );

        // Index for Products
        mongoTemplate.indexOps(Product.class).ensureIndex(
            new Index().on("name", Sort.Direction.ASC)
        );

        // Index for Documents
        mongoTemplate.indexOps(Document.class).ensureIndex(
            new Index().on("name", Sort.Direction.ASC)
        );

        logger.info("MongoDB Indices initialized successfully.");
    }
}

package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InventoryRepository {

    private final MongoTemplate mongoTemplate;

    public InventoryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Product> findAll() {
        return mongoTemplate.findAll(Product.class);
    }

    /**
     * DOD/Zero-Allocation: Using nullable return instead of Optional to avoid object creation in hot paths.
     */
    public Product findByIdOrNull(String id) {
        return mongoTemplate.findById(id, Product.class);
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(findByIdOrNull(id));
    }

    public Product save(Product product) {
        return mongoTemplate.save(product);
    }

    public void delete(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Product.class);
    }
}

package com.example.demo.repository;

import com.example.demo.api.model.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DocumentRepository {

    private final MongoTemplate mongoTemplate;

    public DocumentRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Document> findAll() {
        return mongoTemplate.findAll(Document.class);
    }

    public Document findByIdOrNull(int id) {
        return mongoTemplate.findById(id, Document.class);
    }

    public Optional<Document> findById(int id) {
        return Optional.ofNullable(findByIdOrNull(id));
    }

    public Document save(Document doc) {
        return mongoTemplate.save(doc);
    }

    public void delete(int id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Document.class);
    }
}

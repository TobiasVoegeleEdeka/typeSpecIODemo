package com.example.demo.repository;

import com.example.demo.api.model.Wegbeschreibung;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WegbeschreibungRepository {

    private final MongoTemplate mongoTemplate;

    public WegbeschreibungRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Wegbeschreibung> findAll() {
        return mongoTemplate.findAll(Wegbeschreibung.class);
    }

    public Wegbeschreibung findByIdOrNull(String id) {
        return mongoTemplate.findById(id, Wegbeschreibung.class);
    }

    public Optional<Wegbeschreibung> findById(String id) {
        return Optional.ofNullable(findByIdOrNull(id));
    }

    public Wegbeschreibung save(Wegbeschreibung directions) {
        return mongoTemplate.save(directions);
    }

    public void delete(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Wegbeschreibung.class);
    }
}

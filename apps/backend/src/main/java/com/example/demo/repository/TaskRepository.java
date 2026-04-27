package com.example.demo.repository;

import com.example.demo.api.model.Task;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    private final MongoTemplate mongoTemplate;

    public TaskRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Task> findAll() {
        return mongoTemplate.findAll(Task.class);
    }

    public Task findByIdOrNull(int id) {
        return mongoTemplate.findById(id, Task.class);
    }

    public Optional<Task> findById(int id) {
        return Optional.ofNullable(findByIdOrNull(id));
    }

    public Task save(Task task) {
        return mongoTemplate.save(task);
    }

    public void delete(int id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Task.class);
    }
}

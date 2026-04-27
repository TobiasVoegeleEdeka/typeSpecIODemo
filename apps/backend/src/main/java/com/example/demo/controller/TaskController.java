package com.example.demo.controller;

import com.example.demo.api.model.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.exception.SystemFault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> list() {
        return taskRepository.findAll();
    }

    @PostMapping
    public Task create(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> read(@PathVariable int id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new SystemFault("Task not found", "NOT_FOUND", 404));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable int id, @RequestBody Task taskUpdate) {
        return taskRepository.findById(id).map(existing -> {
            Task updated = new Task(
                id,
                taskUpdate.title() != null ? taskUpdate.title() : existing.title(),
                taskUpdate.description() != null ? taskUpdate.description() : existing.description(),
                taskUpdate.completed()
            );
            taskRepository.save(updated);
            return ResponseEntity.ok(updated);
        }).orElseThrow(() -> new SystemFault("Task not found", "NOT_FOUND", 404));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!taskRepository.findById(id).isPresent()) {
            throw new SystemFault("Task not found", "NOT_FOUND", 404);
        }
        taskRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}

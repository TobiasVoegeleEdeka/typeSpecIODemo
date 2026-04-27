package com.example.demo.controller;

import com.example.demo.api.api.TasksApi;
import com.example.demo.api.model.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.exception.SystemFault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class TaskController implements TasksApi {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> tasksList() {
        return taskRepository.findAll();
    }

    @Override
    public Task tasksCreate(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task tasksRead(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new SystemFault("Task not found", "NOT_FOUND", 404));
    }

    @Override
    public Task tasksUpdate(Integer id, Task taskUpdate) {
        return taskRepository.findById(id).map(existing -> {
            Task updated = new Task(
                    id,
                    taskUpdate.title() != null ? taskUpdate.title() : existing.title(),
                    taskUpdate.description() != null ? taskUpdate.description() : existing.description(),
                    taskUpdate.completed());
            taskRepository.save(updated);
            return updated;
        }).orElseThrow(() -> new SystemFault("Task not found", "NOT_FOUND", 404));
    }

    @Override
    public void tasksDelete(Integer id) {
        if (!taskRepository.findById(id).isPresent()) {
            throw new SystemFault("Task not found", "NOT_FOUND", 404);
        }
        taskRepository.delete(id);
    }
}

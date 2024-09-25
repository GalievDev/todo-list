package dev.galiev.todolist.controller;

import dev.galiev.todolist.model.Task;
import dev.galiev.todolist.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080/")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TasksRepository tasksRepository;

    @GetMapping("/")
    public ResponseEntity<List<Task>> getTasks() {
        return new ResponseEntity<>(tasksRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable long id) {
        return tasksRepository.findById(id)
                .map(task -> new ResponseEntity<>(task, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

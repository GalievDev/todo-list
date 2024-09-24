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
    TasksRepository tasksRepository;

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

    @PostMapping("/")
    public ResponseEntity<Task> addTask(@RequestBody Task task) {
        if (task.getDescription() == null || task.getTitle() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(tasksRepository.save(task), HttpStatus.CREATED);
    }

    @PatchMapping("/")
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        Task taskToUpdate = tasksRepository.findById(task.getId()).orElse(null);
        if(taskToUpdate != null) {
            if (task.getTitle() != null) {
                taskToUpdate.setTitle(task.getTitle());
            }
            if (task.getDescription() != null) {
                taskToUpdate.setDescription(task.getDescription());
            }
            return new ResponseEntity<>(tasksRepository.save(taskToUpdate), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> completeTask(@PathVariable long id) {
        Task task = tasksRepository.findById(id).orElse(null);
        if(task != null) {
            task.setCompleted(!task.isCompleted());
            return new ResponseEntity<>(tasksRepository.save(task), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable long id) {
        Task task = tasksRepository.findById(id).orElse(null);
        if(task != null) {
            tasksRepository.delete(task);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

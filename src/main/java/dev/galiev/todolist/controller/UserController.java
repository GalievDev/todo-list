package dev.galiev.todolist.controller;

import dev.galiev.todolist.model.Task;
import dev.galiev.todolist.repository.TasksRepository;
import dev.galiev.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080/")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<List<Task>> userTasks(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> new ResponseEntity<>(user.getTasks(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    public ResponseEntity<?> addTask(@RequestBody Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (task.getDescription() == null || task.getTitle() == null) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    task.setUser(user);
                    return new ResponseEntity<>(tasksRepository.save(task), HttpStatus.CREATED);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/")
    public ResponseEntity<?> updateTask(@RequestBody Task task) {
        Task taskToUpdate = tasksRepository.findById(task.getId()).orElse(null);
        if(taskToUpdate != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            return userRepository.findByEmail(email)
                    .map(user -> {
                        if (user.hasPermission(taskToUpdate)) {
                            if (task.getTitle() != null) {
                                taskToUpdate.setTitle(task.getTitle());
                            }
                            if (task.getDescription() != null) {
                                taskToUpdate.setDescription(task.getDescription());
                            }
                            return new ResponseEntity<>(tasksRepository.save(taskToUpdate), HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        }
                    })
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> completeTask(@PathVariable long id) {
        Task task = tasksRepository.findById(id).orElse(null);
        if(task != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            userRepository.findByEmail(email)
                    .map(user -> {
                        if (user.hasPermission(task)) {
                            task.setCompleted(!task.isCompleted());
                            return new ResponseEntity<>(tasksRepository.save(task), HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        }
                    })
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable long id) {
        Task task = tasksRepository.findById(id).orElse(null);
        if(task != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            return userRepository.findByEmail(email)
                    .map(user -> {
                        if (user.hasPermission(task)) {
                            tasksRepository.delete(task);
                            return new ResponseEntity<>(tasksRepository.save(task), HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        }
                    })
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

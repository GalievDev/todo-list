package dev.galiev.todolist.controller;

import dev.galiev.todolist.model.Comment;
import dev.galiev.todolist.model.Role;
import dev.galiev.todolist.model.Task;
import dev.galiev.todolist.model.User;
import dev.galiev.todolist.repository.CommentsRepository;
import dev.galiev.todolist.repository.TasksRepository;
import dev.galiev.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080/")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private TasksRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentsRepository commentRepository;

    // Helper method to check if the user is an admin
    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    @GetMapping("/")
    public ResponseEntity<?> getTasks(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long executorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks;

        if (authorId != null) {
            tasks = taskRepository.findByAuthorId(authorId, pageable);
        } else if (executorId != null) {
            tasks = taskRepository.findByAssignedUserId(executorId, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> addTask(@RequestBody Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (!isAdmin(user)) {
                        return new ResponseEntity<>("Only admins can create tasks", HttpStatus.FORBIDDEN);
                    }

                    if (task.getTitle() == null || task.getDescription() == null) {
                        return new ResponseEntity<>("Title and description are required", HttpStatus.BAD_REQUEST);
                    }

                    task.setAuthor(user);
                    return new ResponseEntity<>(taskRepository.save(task), HttpStatus.CREATED);
                })
                .orElse(new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return taskRepository.findById(id)
                .map(task -> userRepository.findByEmail(email)
                        .map(user -> {
                            if (!isAdmin(user) || !user.hasPermission(task)) {
                                return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
                            }

                            if (updatedTask.getTitle() != null) task.setTitle(updatedTask.getTitle());
                            if (updatedTask.getDescription() != null) task.setDescription(updatedTask.getDescription());
                            if (isAdmin(user)) {
                                if (updatedTask.getPriority() != null) task.setPriority(updatedTask.getPriority());
                                if (updatedTask.getStatus() != null) task.setStatus(updatedTask.getStatus());
                                if (updatedTask.getAssignedUser() != null) task.setAssignedUser(updatedTask.getAssignedUser());
                            }

                            return new ResponseEntity<>(taskRepository.save(task), HttpStatus.OK);
                        })
                        .orElse(new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return taskRepository.findById(id)
                .map(task -> userRepository.findByEmail(email)
                        .map(user -> {
                            if (!isAdmin(user) && !user.hasPermission(task)) {
                                return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
                            }

                            taskRepository.delete(task);
                            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                        })
                        .orElse(new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody Comment comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return taskRepository.findById(id)
                .map(task -> userRepository.findByEmail(email)
                        .map(user -> {
                            if (!isAdmin(user) && !user.hasPermission(task)) {
                                return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
                            }

                            comment.setTask(task);
                            comment.setUser(user);
                            return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);
                        })
                        .orElse(new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> new ResponseEntity<>(task.getComments(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

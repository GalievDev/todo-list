package dev.galiev.todolist;

import dev.galiev.todolist.controller.AuthController;
import dev.galiev.todolist.controller.UserController;
import dev.galiev.todolist.dto.AuthRequest;
import dev.galiev.todolist.dto.AuthResponse;
import dev.galiev.todolist.dto.RegisterRequest;
import dev.galiev.todolist.model.Role;
import dev.galiev.todolist.model.Task;
import dev.galiev.todolist.model.User;
import dev.galiev.todolist.repository.TasksRepository;
import dev.galiev.todolist.repository.UserRepository;
import dev.galiev.todolist.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class TodoListApplicationTests {

    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    @InjectMocks
    private AuthController authController;

    private User user;
    private Task task;
    private String token;

    @BeforeEach
    void contextStart() {
        MockitoAnnotations.openMocks(this);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        user = new User();
        user.setId(1L);
        user.setEmail("bob@gmail.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUser(user);

        token = "mocked_jwt_token";
    }

    @Test
    void testRegisterUser_Success() {
        RegisterRequest registerRequest = new RegisterRequest("Bob", "bob@gmail.com", "password", Role.USER);
        AuthResponse authResponse = new AuthResponse(token);

        when(authenticationService.register(registerRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody().getToken());
    }

    @Test
    void testAuthenticateUser_Success() {
        AuthRequest authRequest = new AuthRequest("bob@gmail.com", "password");
        AuthResponse authResponse = new AuthResponse(token);

        when(authenticationService.authenticate(authRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.authenticate(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody().getToken());
    }

    @Test
    void testAddTask_AfterAuthentication() {
        when(authentication.getName()).thenReturn("bob@gmail.com");
        when(userRepository.findByEmail("bob@gmail.com")).thenReturn(Optional.of(user));
        when(tasksRepository.save(any(Task.class))).thenReturn(task);

        ResponseEntity<?> response = userController.addTask(task);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void testUpdateTask_AfterAuthentication() {
        when(authentication.getName()).thenReturn("bob@gmail.com");
        when(userRepository.findByEmail("bob@gmail.com")).thenReturn(Optional.of(user));
        when(tasksRepository.save(task)).thenReturn(task);

        ResponseEntity<?> response = userController.updateTask(task);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void testDeleteTask_AfterAuthentication() {
        when(authentication.getName()).thenReturn("bob@gmail.com");
        when(userRepository.findByEmail("bob@gmail.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.deleteTask(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}

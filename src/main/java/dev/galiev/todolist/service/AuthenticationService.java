package dev.galiev.todolist.service;

import dev.galiev.todolist.dto.AuthRequest;
import dev.galiev.todolist.dto.AuthResponse;
import dev.galiev.todolist.dto.RegisterRequest;

public interface AuthenticationService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse authenticate(AuthRequest request);
}

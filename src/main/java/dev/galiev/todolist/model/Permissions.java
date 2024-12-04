package dev.galiev.todolist.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permissions {
    USERS_READ("users:read"),
    USERS_CREATE("users:create"),
    USERS_UPDATE("users:update"),
    USERS_DELETE("users:delete"),
    ADMIN_READ("admins:read"),
    ADMIN_CREATE("admins:create"),
    ADMIN_UPDATE("admins:update"),
    ADMIN_DELETE("admins:delete"),
    ;

    private final String permission;
}

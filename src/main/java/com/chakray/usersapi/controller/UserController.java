package com.chakray.usersapi.controller;

import com.chakray.usersapi.dto.CreateUserRequest;
import com.chakray.usersapi.dto.UpdateUserRequest;
import com.chakray.usersapi.dto.UserResponse;
import com.chakray.usersapi.model.User;
import com.chakray.usersapi.service.UserMapper;
import com.chakray.usersapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management resource")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    @Operation(summary = "List users",
            description = "Optionally sorted by sortedBy=[email|id|name|phone|tax_id|created_at] "
                    + "and/or filtered by filter=[attribute]+[co|eq|sw|ew]+[value]")
    public List<UserResponse> getUsers(
            @RequestParam(name = "sortedBy", required = false) String sortedBy,
            @RequestParam(name = "filter", required = false) String filter) {
        List<User> users = userService.getUsers(sortedBy, filter);
        return userMapper.toResponseList(users);
    }

    @PostMapping
    @Operation(summary = "Create a user")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User created = userService.create(request, userMapper);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(created));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user attributes by id")
    public UserResponse update(@PathVariable UUID id,
                               @Valid @RequestBody UpdateUserRequest request) {
        User updated = userService.update(id, request, userMapper);
        return userMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by id")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

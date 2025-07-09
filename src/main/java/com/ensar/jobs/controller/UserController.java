package com.ensar.jobs.controller;

import com.ensar.jobs.dto.UserDTO;
import com.ensar.jobs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")   
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Updates an existing user's information")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user's information by their ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/paged")
    @Operation(summary = "Get paginated users", description = "Retrieves a paginated list of users")
    public ResponseEntity<Page<UserDTO>> getPagedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getPagedUsers(pageable));
    }

    @GetMapping("/jobseekers")
    @Operation(summary = "Get all jobseeker users", description = "Retrieves a list of all users with the role of Jobseeker")
    public ResponseEntity<List<UserDTO>> getAllJobSeekerUsers() {
        return ResponseEntity.ok(userService.getAllJobSeekerUsers());
    }

    @GetMapping("/jobseekers/paged")
    @Operation(summary = "Get paginated jobseeker users", description = "Retrieves a paginated list of users with the role of Jobseeker")
    public ResponseEntity<Page<UserDTO>> getPagedJobSeekerUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getPagedJobSeekerUsers(pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user by their ID")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle user status", description = "Toggles a user's disabled status")
    public ResponseEntity<UserDTO> toggleUserStatus(@PathVariable String id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }
} 
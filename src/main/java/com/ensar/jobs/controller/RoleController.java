package com.ensar.jobs.controller;

import com.ensar.jobs.dto.RoleDTO;
import com.ensar.jobs.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role", description = "Role management APIs")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Get all roles", description = "Retrieve a list of all roles")
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved roles",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))
    )
    @GetMapping
    public List<RoleDTO> getAllRoles() {
        return roleService.getAllRoles();
    }

    @Operation(summary = "Get role by ID", description = "Retrieve a role by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable String id) {
        RoleDTO dto = roleService.getRoleById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get role by name", description = "Retrieve a role by its name")
    @GetMapping("/name/{roleName}")
    public ResponseEntity<RoleDTO> getRoleByName(
            @Parameter(description = "Name of the role") @PathVariable String roleName) {
        RoleDTO role = roleService.getRoleByName(roleName);
        if (role != null) {
            return ResponseEntity.ok(role);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new role", description = "Create a new role with the provided data")
    @PostMapping
    public RoleDTO createRole(@RequestBody RoleDTO dto) {
        return roleService.createRole(dto);
    }

    @Operation(summary = "Update a role", description = "Update an existing role by its ID")
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable String id, @RequestBody RoleDTO dto) {
        RoleDTO updated = roleService.updateRole(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a role", description = "Delete a role by its ID")
    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
    }
} 
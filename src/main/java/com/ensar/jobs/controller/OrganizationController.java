package com.ensar.jobs.controller;

import com.ensar.jobs.dto.OrganizationDTO;
import com.ensar.jobs.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organization", description = "Organization management APIs")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Operation(
        summary = "Retrieve all organizations",
        description = "Get a list of all organizations in the system."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved organizations",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))
    )
    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        List<OrganizationDTO> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @Operation(
        summary = "Retrieve an organization by ID",
        description = "Get an organization by its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organization found"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(
            @Parameter(description = "ID of the organization to retrieve") @PathVariable String id) {
        OrganizationDTO organization = organizationService.getOrganizationById(id);
        if (organization != null) {
            return ResponseEntity.ok(organization);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Create a new organization",
        description = "Create a new organization with the provided data."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Organization created successfully"
    )
    @PostMapping
    public ResponseEntity<OrganizationDTO> createOrganization(
            @Parameter(description = "Organization data to create") @RequestBody OrganizationDTO organizationDTO) {
        OrganizationDTO createdOrganization = organizationService.createOrganization(organizationDTO);
        return ResponseEntity.ok(createdOrganization);
    }

    @Operation(
        summary = "Update an organization",
        description = "Update an existing organization by its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organization updated successfully"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> updateOrganization(
            @Parameter(description = "ID of the organization to update") @PathVariable String id,
            @Parameter(description = "Updated organization data") @RequestBody OrganizationDTO organizationDTO) {
        OrganizationDTO updatedOrganization = organizationService.updateOrganization(id, organizationDTO);
        if (updatedOrganization != null) {
            return ResponseEntity.ok(updatedOrganization);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Delete an organization",
        description = "Delete an organization by its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organization deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(
            @Parameter(description = "ID of the organization to delete") @PathVariable String id) {
        boolean deleted = organizationService.deleteOrganization(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 
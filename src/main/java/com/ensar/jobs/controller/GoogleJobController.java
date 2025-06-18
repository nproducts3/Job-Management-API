package com.ensar.jobs.controller;

import com.ensar.jobs.dto.GoogleJobDTO;
import com.ensar.jobs.service.GoogleJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/google-jobs")
@RequiredArgsConstructor
@Tag(name = "Google Jobs", description = "Google Jobs management APIs")
public class GoogleJobController {

    private final GoogleJobService googleJobService;

    @PostMapping
    @Operation(summary = "Create a new Google job")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYER')")
    public ResponseEntity<GoogleJobDTO> createGoogleJob(@Valid @RequestBody GoogleJobDTO googleJobDTO) {
        return new ResponseEntity<>(googleJobService.createGoogleJob(googleJobDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Google job")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYER')")
    public ResponseEntity<GoogleJobDTO> updateGoogleJob(@PathVariable String id, @Valid @RequestBody GoogleJobDTO googleJobDTO) {
        return ResponseEntity.ok(googleJobService.updateGoogleJob(id, googleJobDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Google job by ID")
    public ResponseEntity<GoogleJobDTO> getGoogleJobById(@PathVariable String id) {
        return ResponseEntity.ok(googleJobService.getGoogleJobById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Google job")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<Void> deleteGoogleJob(@PathVariable String id) {
        googleJobService.deleteGoogleJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all Google jobs")
    public ResponseEntity<List<GoogleJobDTO>> getAllGoogleJobs() {
        return ResponseEntity.ok(googleJobService.getAllGoogleJobs());
    }
} 
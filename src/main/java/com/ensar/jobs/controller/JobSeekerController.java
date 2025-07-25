package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobSeekerDTO;
import com.ensar.jobs.service.JobSeekerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/job-seekers")
@Tag(name = "Job Seekers", description = "CRUD operations for job seekers")
public class JobSeekerController {
    @Autowired
    private JobSeekerService jobSeekerService;
    @Autowired
    private com.ensar.jobs.repository.UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create a new job seeker")
    public ResponseEntity<JobSeekerDTO> create(@RequestBody JobSeekerDTO dto, Authentication authentication) {
        // Always use the logged-in user's UUID, not email
        String principal = authentication.getName();
        // Try to find user by email (principal)
        com.ensar.jobs.entity.User user = userRepository.findByEmail(principal)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found with email: " + principal));
        dto.setUserId(user.getId());
        return ResponseEntity.ok(jobSeekerService.createJobSeeker(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job seeker")
    public ResponseEntity<JobSeekerDTO> update(@PathVariable String id, @RequestBody JobSeekerDTO dto) {
        return ResponseEntity.ok(jobSeekerService.updateJobSeeker(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job seeker by ID")
    public ResponseEntity<JobSeekerDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(jobSeekerService.getJobSeekerById(id));
    }

    @GetMapping
    @Operation(summary = "Get all job seekers")
    public ResponseEntity<List<JobSeekerDTO>> getAll() {
        return ResponseEntity.ok(jobSeekerService.getAllJobSeekers());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job seeker")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        jobSeekerService.deleteJobSeeker(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to get jobSeekerId for current authenticated user
    @GetMapping("/me/id")
    @Operation(summary = "Get jobSeekerId for current user")
    public ResponseEntity<String> getMyJobSeekerId(Authentication authentication) {
        String principal = authentication.getName();
        com.ensar.jobs.entity.User user = userRepository.findByEmail(principal)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found with email: " + principal));
        String jobSeekerId = jobSeekerService.getJobSeekerIdByUserId(user.getId());
        return ResponseEntity.ok(jobSeekerId);
    }
} 
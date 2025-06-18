package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobSeekerEducationDTO;
import com.ensar.jobs.service.JobSeekerEducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/job-seeker-educations")
@Tag(name = "Job Seeker Educations", description = "CRUD operations for job seeker educations")
public class JobSeekerEducationController {
    @Autowired
    private JobSeekerEducationService educationService;

    @PostMapping
    @Operation(summary = "Create a new job seeker education")
    public ResponseEntity<JobSeekerEducationDTO> create(@RequestBody JobSeekerEducationDTO dto) {
        return ResponseEntity.ok(educationService.createJobSeekerEducation(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job seeker education")
    public ResponseEntity<JobSeekerEducationDTO> update(@PathVariable String id, @RequestBody JobSeekerEducationDTO dto) {
        return ResponseEntity.ok(educationService.updateJobSeekerEducation(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job seeker education by ID")
    public ResponseEntity<JobSeekerEducationDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(educationService.getJobSeekerEducationById(id));
    }

    @GetMapping
    @Operation(summary = "Get all job seeker educations")
    public ResponseEntity<List<JobSeekerEducationDTO>> getAll() {
        return ResponseEntity.ok(educationService.getAllJobSeekerEducations());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job seeker education")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        educationService.deleteJobSeekerEducation(id);
        return ResponseEntity.noContent().build();
    }
} 
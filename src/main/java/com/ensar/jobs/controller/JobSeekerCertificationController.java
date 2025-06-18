package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobSeekerCertificationDTO;
import com.ensar.jobs.service.JobSeekerCertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/job-seeker-certifications")
@Tag(name = "Job Seeker Certifications", description = "CRUD operations for job seeker certifications")
public class JobSeekerCertificationController {
    @Autowired
    private JobSeekerCertificationService certificationService;

    @PostMapping
    @Operation(summary = "Create a new job seeker certification")
    public ResponseEntity<JobSeekerCertificationDTO> create(@RequestBody JobSeekerCertificationDTO dto) {
        return ResponseEntity.ok(certificationService.createJobSeekerCertification(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job seeker certification")
    public ResponseEntity<JobSeekerCertificationDTO> update(@PathVariable String id, @RequestBody JobSeekerCertificationDTO dto) {
        return ResponseEntity.ok(certificationService.updateJobSeekerCertification(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job seeker certification by ID")
    public ResponseEntity<JobSeekerCertificationDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(certificationService.getJobSeekerCertificationById(id));
    }

    @GetMapping
    @Operation(summary = "Get all job seeker certifications")
    public ResponseEntity<List<JobSeekerCertificationDTO>> getAll() {
        return ResponseEntity.ok(certificationService.getAllJobSeekerCertifications());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job seeker certification")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        certificationService.deleteJobSeekerCertification(id);
        return ResponseEntity.noContent().build();
    }
} 
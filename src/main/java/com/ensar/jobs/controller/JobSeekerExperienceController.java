package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobSeekerExperienceDTO;
import com.ensar.jobs.service.JobSeekerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/job-seeker-experiences")
@Tag(name = "Job Seeker Experiences", description = "CRUD operations for job seeker experiences")
public class JobSeekerExperienceController {
    @Autowired
    private JobSeekerExperienceService experienceService;

    @PostMapping
    @Operation(summary = "Create a new job seeker experience")
    public ResponseEntity<JobSeekerExperienceDTO> create(@RequestBody JobSeekerExperienceDTO dto) {
        return ResponseEntity.ok(experienceService.createJobSeekerExperience(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job seeker experience")
    public ResponseEntity<JobSeekerExperienceDTO> update(@PathVariable String id, @RequestBody JobSeekerExperienceDTO dto) {
        return ResponseEntity.ok(experienceService.updateJobSeekerExperience(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job seeker experience by ID")
    public ResponseEntity<List<JobSeekerExperienceDTO>> getById(@PathVariable String id) {
        return ResponseEntity.ok(experienceService.getJobSeekerExperienceById(id));
    }

    @GetMapping("/by-jobseeker")
    @Operation(summary = "Get all job seeker experiences by jobSeekerId")
    public ResponseEntity<List<JobSeekerExperienceDTO>> getAllByJobSeekerExperienceId(@RequestParam String jobSeekerId) {
        return ResponseEntity.ok(experienceService.getAllByJobSeekerExperienceId(jobSeekerId));
    }

    @GetMapping
    @Operation(summary = "Get all job seeker experiences")
    public ResponseEntity<List<JobSeekerExperienceDTO>> getAll() {
        return ResponseEntity.ok(experienceService.getAllJobSeekerExperiences());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job seeker experience")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        experienceService.deleteJobSeekerExperience(id);
        return ResponseEntity.noContent().build();
    }
} 
package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobDTO;
import com.ensar.jobs.entity.Job.JobStatus;
import com.ensar.jobs.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Jobs management APIs")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @Operation(summary = "Create a new job")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_JOBSEEKER')")
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody JobDTO jobDTO) {
        return new ResponseEntity<>(jobService.createJob(jobDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing job")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_JOBSEEKER')")
    public ResponseEntity<JobDTO> updateJob(@PathVariable String id, @Valid @RequestBody JobDTO jobDTO) {
        return ResponseEntity.ok(jobService.updateJob(id, jobDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a job by ID")
    public ResponseEntity<JobDTO> getJobById(@PathVariable String id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all jobs")
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get jobs by status")
    public ResponseEntity<List<JobDTO>> getJobsByStatus(@PathVariable JobStatus status) {
        return ResponseEntity.ok(jobService.getJobsByStatus(status));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get jobs by company")
    public ResponseEntity<List<JobDTO>> getJobsByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(jobService.getJobsByCompany(companyId));
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "Get jobs by city")
    public ResponseEntity<List<JobDTO>> getJobsByCity(@PathVariable Integer cityId) {
        return ResponseEntity.ok(jobService.getJobsByCity(cityId));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured jobs")
    public ResponseEntity<List<JobDTO>> getFeaturedJobs() {
        return ResponseEntity.ok(jobService.getFeaturedJobs());
    }

    @GetMapping("/urgent")
    @Operation(summary = "Get urgent jobs")
    public ResponseEntity<List<JobDTO>> getUrgentJobs() {
        return ResponseEntity.ok(jobService.getUrgentJobs());
    }

    @GetMapping("/recruiter/{recruiterId}")
    @Operation(summary = "Get jobs by recruiter")
    public ResponseEntity<List<JobDTO>> getJobsByRecruiter(@PathVariable String recruiterId) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiterId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search jobs with filters")
    public ResponseEntity<List<JobDTO>> searchJobs(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) String searchTerm) {
        return ResponseEntity.ok(jobService.searchJobs(skill, minExperience, maxExperience, cityId, searchTerm));
    }

    @GetMapping("/salary-range")
    @Operation(summary = "Get jobs by salary range")
    public ResponseEntity<List<JobDTO>> getJobsBySalaryRange(
            @RequestParam Integer minSalary,
            @RequestParam Integer maxSalary) {
        return ResponseEntity.ok(jobService.getJobsBySalaryRange(minSalary, maxSalary));
    }

    @GetMapping("/experience-range")
    @Operation(summary = "Get jobs by experience range")
    public ResponseEntity<List<JobDTO>> getJobsByExperienceRange(
            @RequestParam Integer minExperience,
            @RequestParam Integer maxExperience) {
        return ResponseEntity.ok(jobService.getJobsByExperienceRange(minExperience, maxExperience));
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get jobs about to expire")
    public ResponseEntity<List<JobDTO>> getJobsAboutToExpire(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam JobStatus status) {
        return ResponseEntity.ok(jobService.getJobsAboutToExpire(start, end, status));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recently posted jobs")
    public ResponseEntity<List<JobDTO>> getRecentlyPostedJobs(@RequestParam LocalDateTime date) {
        return ResponseEntity.ok(jobService.getRecentlyPostedJobs(date));
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Get job count by status")
    public ResponseEntity<Long> getJobCountByStatus(@PathVariable JobStatus status) {
        return ResponseEntity.ok(jobService.getJobCountByStatus(status));
    }

    @GetMapping("/count/company/{companyId}")
    @Operation(summary = "Get job count by company")
    public ResponseEntity<Long> getJobCountByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(jobService.getJobCountByCompany(companyId));
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<List<JobDTO>> searchJobsAdvanced(
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer cityId) {
        List<JobDTO> jobs = jobService.searchJobsBySkillsExperienceAndLocation(
                skills, minExperience, maxExperience, cityId);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/search/{keyword}")
    @Operation(summary = "Search jobs by keyword in path")
    public ResponseEntity<List<JobDTO>> searchJobsByKeyword(@PathVariable String keyword) {
        List<JobDTO> jobs = jobService.searchJobs(null, null, null, null, keyword.trim());
        return ResponseEntity.ok(jobs);
    }
} 
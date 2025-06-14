package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobTitleDTO;
import com.ensar.jobs.service.JobTitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/job-titles")
@CrossOrigin(origins = "*")
@Tag(name = "Job Title Management", description = "APIs for managing job titles")
public class JobTitleController {

    private final JobTitleService jobTitleService;

    public JobTitleController(JobTitleService jobTitleService) {
        this.jobTitleService = jobTitleService;
    }

    @Operation(
        summary = "Create a new job title",
        description = "Creates a new job title with the provided information"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Job title created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or title already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<JobTitleDTO> createJobTitle(
            @Parameter(description = "Job title details", required = true)
            @Valid @RequestBody JobTitleDTO jobTitleDTO) {
        JobTitleDTO createdJobTitle = jobTitleService.createJobTitle(jobTitleDTO);
        return new ResponseEntity<>(createdJobTitle, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Update an existing job title",
        description = "Updates the job title with the specified ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job title updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or title already exists"),
        @ApiResponse(responseCode = "404", description = "Job title not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<JobTitleDTO> updateJobTitle(
            @Parameter(description = "Job title ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated job title details", required = true)
            @Valid @RequestBody JobTitleDTO jobTitleDTO) {
        JobTitleDTO updatedJobTitle = jobTitleService.updateJobTitle(id, jobTitleDTO);
        return ResponseEntity.ok(updatedJobTitle);
    }

    @Operation(
        summary = "Get a job title by ID",
        description = "Retrieves a job title by its ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Job title found",
            content = @Content(schema = @Schema(implementation = JobTitleDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "Job title not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobTitleDTO> getJobTitleById(
            @Parameter(description = "Job title ID", required = true)
            @PathVariable Long id) {
        JobTitleDTO jobTitleDTO = jobTitleService.getJobTitleById(id);
        return ResponseEntity.ok(jobTitleDTO);
    }

    @Operation(
        summary = "Get all job titles",
        description = "Retrieves a list of all job titles"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of job titles retrieved successfully",
            content = @Content(schema = @Schema(implementation = JobTitleDTO.class))
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<JobTitleDTO>> getAllJobTitles() {
        List<JobTitleDTO> jobTitles = jobTitleService.getAllJobTitles();
        return ResponseEntity.ok(jobTitles);
    }

    @Operation(
        summary = "Delete a job title",
        description = "Deletes the job title with the specified ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Job title deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Job title not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobTitle(
            @Parameter(description = "Job title ID", required = true)
            @PathVariable Long id) {
        jobTitleService.deleteJobTitle(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
} 
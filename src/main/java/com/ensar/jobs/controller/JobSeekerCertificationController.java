package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobSeekerCertificationDTO;
import com.ensar.jobs.service.JobSeekerCertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new job seeker certification with file upload")
    public ResponseEntity<JobSeekerCertificationDTO> createWithFile(
            @Parameter(description = "Certification file (PDF, DOCX, JPG, PNG)") 
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Certification name") 
            @RequestParam("certificationName") String certificationName,
            @Parameter(description = "Job seeker ID") 
            @RequestParam("jobSeekerId") String jobSeekerId,
            @Parameter(description = "Issued date (YYYY-MM-DD or MM/DD/YYYY)") 
            @RequestParam(value = "issuedDate", required = false) String issuedDate,
            @Parameter(description = "Expiry date (YYYY-MM-DD or MM/DD/YYYY)") 
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @Parameter(description = "Issuing organization") 
            @RequestParam(value = "issuingOrganization", required = false) String issuingOrganization) throws IOException {
        
        JobSeekerCertificationDTO dto = new JobSeekerCertificationDTO();
        dto.setCertificationName(certificationName);
        dto.setJobSeekerId(jobSeekerId);
        dto.setIssuingOrganization(issuingOrganization);
        
        if (issuedDate != null && !issuedDate.isEmpty()) {
            dto.setIssuedDate(parseDate(issuedDate));
        }
        if (expiryDate != null && !expiryDate.isEmpty()) {
            dto.setExpiryDate(parseDate(expiryDate));
        }
        
        return ResponseEntity.ok(certificationService.createJobSeekerCertificationWithFile(file, dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job seeker certification")
    public ResponseEntity<JobSeekerCertificationDTO> update(@PathVariable String id, @RequestBody JobSeekerCertificationDTO dto) {
        return ResponseEntity.ok(certificationService.updateJobSeekerCertification(id, dto));
    }

    @PutMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a job seeker certification with file upload")
    public ResponseEntity<JobSeekerCertificationDTO> updateWithFile(
            @PathVariable String id,
            @Parameter(description = "Certification file (PDF, DOCX, JPG, PNG)") 
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Certification name") 
            @RequestParam("certificationName") String certificationName,
            @Parameter(description = "Job seeker ID") 
            @RequestParam("jobSeekerId") String jobSeekerId,
            @Parameter(description = "Issued date (YYYY-MM-DD or MM/DD/YYYY)") 
            @RequestParam(value = "issuedDate", required = false) String issuedDate,
            @Parameter(description = "Expiry date (YYYY-MM-DD or MM/DD/YYYY)") 
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @Parameter(description = "Issuing organization") 
            @RequestParam(value = "issuingOrganization", required = false) String issuingOrganization) throws IOException {
        
        JobSeekerCertificationDTO dto = new JobSeekerCertificationDTO();
        dto.setCertificationName(certificationName);
        dto.setJobSeekerId(jobSeekerId);
        dto.setIssuingOrganization(issuingOrganization);
        
        if (issuedDate != null && !issuedDate.isEmpty()) {
            dto.setIssuedDate(parseDate(issuedDate));
        }
        if (expiryDate != null && !expiryDate.isEmpty()) {
            dto.setExpiryDate(parseDate(expiryDate));
        }
        
        return ResponseEntity.ok(certificationService.updateJobSeekerCertificationWithFile(id, file, dto));
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

    @GetMapping("/job-seeker/{jobSeekerId}")
    @Operation(summary = "Get certifications by job seeker ID")
    public ResponseEntity<List<JobSeekerCertificationDTO>> getByJobSeekerId(@PathVariable String jobSeekerId) {
        return ResponseEntity.ok(certificationService.getCertificationsByJobSeekerId(jobSeekerId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job seeker certification")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        certificationService.deleteJobSeekerCertification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Parse date string in multiple formats
     * Supports: yyyy-MM-dd, MM/dd/yyyy, MM-dd-yyyy
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        // Try different date formats
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
        };
        
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Continue to next format
            }
        }
        
        throw new IllegalArgumentException("Unable to parse date: " + dateStr + 
            ". Supported formats: yyyy-MM-dd, MM/dd/yyyy, MM-dd-yyyy, dd/MM/yyyy, dd-MM-yyyy");
    }
} 
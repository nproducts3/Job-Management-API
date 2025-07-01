package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobResumeDTO;
import com.ensar.jobs.service.JobResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/job-resumes")
@RequiredArgsConstructor
public class JobResumeController {
    private final JobResumeService jobResumeService;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @PostMapping(value = "/upload/{googleJobId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE','ROLE_JOBSEEKER')")
    public ResponseEntity<JobResumeDTO> uploadResume(
            @PathVariable String googleJobId,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().build();
        }

        JobResumeDTO result = jobResumeService.uploadAndProcessResume(file, UUID.fromString(googleJobId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/job/{googleJobId}")
    public ResponseEntity<List<JobResumeDTO>> getResumesByJobId(@PathVariable String googleJobId) {
        List<JobResumeDTO> resumes = jobResumeService.getResumesByJobId(UUID.fromString(googleJobId));
        return ResponseEntity.ok(resumes);
    }
       
    @DeleteMapping("/job/{googleJobId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteResume(@PathVariable String googleJobId) {
        jobResumeService.deleteResumeForJob(UUID.fromString(googleJobId));
        return ResponseEntity.ok().build();
    }
} 
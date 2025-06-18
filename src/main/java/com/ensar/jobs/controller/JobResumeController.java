package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobResumeDTO;
import com.ensar.jobs.service.JobResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
    public ResponseEntity<JobResumeDTO> uploadResume(
            @PathVariable String googleJobId,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().build();
        }

        JobResumeDTO result = jobResumeService.uploadAndProcessResume(file, googleJobId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/job/{googleJobId}")
    public ResponseEntity<List<JobResumeDTO>> getResumesByJobId(@PathVariable String googleJobId) {
        List<JobResumeDTO> resumes = jobResumeService.getResumesByJobId(googleJobId);
        return ResponseEntity.ok(resumes);
    }
       
    @DeleteMapping("/job/{googleJobId}")
    public ResponseEntity<Void> deleteResume(@PathVariable String googleJobId) {
        jobResumeService.deleteResumeForJob(googleJobId);
        return ResponseEntity.ok().build();
    }
} 
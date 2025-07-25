package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobResumeDTO;
import com.ensar.jobs.service.JobResumeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/resume-analysis")
public class JobResumeController_download {
    private final JobResumeService jobResumeService;

    public JobResumeController_download(JobResumeService jobResumeService) {
        this.jobResumeService = jobResumeService;
    }

    @GetMapping("/download/{jobSeekerId}/{googleJobId}")
    @Operation(summary = "Download improved resume as TXT",
               description = "Download the latest improved resume for the given job seeker and job as a plain text file.")
    public ResponseEntity<Resource> downloadImprovedResume(
            @PathVariable String jobSeekerId,
            @PathVariable String googleJobId) {
        JobResumeDTO resumeDTO = jobResumeService.getResumeTextAndMatchPercentage(jobSeekerId, googleJobId);
        if (resumeDTO == null || resumeDTO.getResumeText() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] resumeBytes = resumeDTO.getResumeText().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(resumeBytes);
        String filename = "improved_resume_" + jobSeekerId + "_" + googleJobId + ".txt";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(resumeBytes.length)
                .body(resource);
    }
}

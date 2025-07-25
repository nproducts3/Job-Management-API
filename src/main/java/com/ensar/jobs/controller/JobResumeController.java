package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobMatchResultDTO;
import com.ensar.jobs.dto.JobResumeDTO;
import com.ensar.jobs.dto.ResumeAnalysisDTO;
import com.ensar.jobs.service.JobResumeService;
import com.ensar.jobs.service.JobSeekerService;
import com.ensar.jobs.entity.JobSeeker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/resume-analysis")
@RequiredArgsConstructor
@Tag(name = "Resume Analysis", description = "Resume analysis and job matching APIs")
public class JobResumeController {

    private final JobResumeService jobResumeService;
    private final JobSeekerService jobSeekerService;

    @GetMapping("/resume-text-match")
    @Operation(
        summary = "Get resume text and match percentage by jobSeekerId and googleJobId",
        description = "Returns the resume text and match percentage for the given jobSeekerId and googleJobId"
    )
    public ResponseEntity<JobResumeDTO> getResumeTextAndMatch(
        @RequestParam String jobSeekerId,
        @RequestParam String googleJobId
    ) {
        return ResponseEntity.ok(jobResumeService.getResumeTextAndMatchPercentage(jobSeekerId, googleJobId));
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Analyze resume against all jobs", 
               description = "Upload a resume and get comprehensive analysis against all available jobs")
    public ResponseEntity<ResumeAnalysisDTO> analyzeResume(
            @Parameter(description = "Resume file (PDF or DOCX)") 
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Job seeker ID") 
            @RequestParam("jobSeekerId") String jobSeekerId) throws IOException {
        
        ResumeAnalysisDTO analysis = jobResumeService.analyzeResumeAgainstAllJobs(file, jobSeekerId);
        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/top-matches")
    @Operation(summary = "Get top matching jobs for resume text", 
               description = "Analyze resume text and return top matching jobs")
    public ResponseEntity<List<JobMatchResultDTO>> getTopMatchingJobs(
            @Parameter(description = "Resume text content") 
            @RequestParam("resumeText") String resumeText,
            @Parameter(description = "Number of top matches to return (default: 10)") 
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @Parameter(description = "Job Seeker ID")
            @RequestParam("jobSeekerId") String jobSeekerId) {
        
        JobSeeker jobSeeker = jobSeekerService.getJobSeekerEntityById(jobSeekerId);
        List<JobMatchResultDTO> topMatches = jobResumeService.getTopMatchingJobs(resumeText, limit, jobSeeker);
        return ResponseEntity.ok(topMatches);
    }

    @GetMapping("/skills")
    @Operation(summary = "Extract skills from resume text", 
               description = "Extract and categorize skills from resume text using AI backend")
    public ResponseEntity<Map<String, Object>> extractSkills(
            @Parameter(description = "Resume text content") 
            @RequestParam("resumeText") String resumeText) {
        // Send resumeText to Python backend /analyze endpoint
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        String aiUrl = "http://localhost:8000/analyze";
        java.util.Map<String, Object> request = new java.util.HashMap<>();
        request.put("resume_text", resumeText);
        request.put("jobs", new java.util.ArrayList<>()); // No jobs needed for skill extraction only
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(request, headers);
        org.springframework.http.ResponseEntity<java.util.Map> aiResponse = restTemplate.postForEntity(aiUrl, entity, java.util.Map.class);
        java.util.Map<String, Object> aiBody = aiResponse.getBody();
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        if (aiBody != null) {
            if (aiBody.get("skills") != null) {
                response.put("extractedSkills", aiBody.get("skills"));
            }
            if (aiBody.get("skills_by_category") != null) {
                response.put("skillsByCategory", aiBody.get("skills_by_category"));
            }
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/uploaded-files")
    @Operation(summary = "List uploaded resume files", description = "Get information about all files in the uploads/resumes directory.")
    public ResponseEntity<List<Map<String, Object>>> listUploadedFiles() {
        List<Map<String, Object>> filesInfo = new ArrayList<>();
        try {
            Path uploadDir = Paths.get("uploads/resumes");
            if (Files.exists(uploadDir)) {
                Files.list(uploadDir).forEach(path -> {
                    try {
                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("fileName", path.getFileName().toString());
                        fileInfo.put("size", attrs.size());
                        fileInfo.put("createdTime", attrs.creationTime().toString());
                        fileInfo.put("lastModifiedTime", attrs.lastModifiedTime().toString());
                        filesInfo.add(fileInfo);
                    } catch (Exception e) {
                        // skip file if error
                    }
                });
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(filesInfo);
    }

    @Data
    public static class AutoImproveRequest {
        private String action; // "analyze" or "apply_suggestion"
        private String resumeText;
        private UUID googleJobId; // was jobId
        private String jobSeekerId;
        private String suggestion; // optional, only for apply_suggestion
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AutoImproveResponse {
        private String resumeText;
        private BigDecimal matchPercentage;
        private List<String> suggestions;
        private boolean canDownload;
    }

    @PostMapping("/auto-improve")
    @Operation(summary = "Analyze and iteratively improve resume for a job using AI suggestions",
               description = "Analyze resume, return match %, suggestions, and allow applying suggestions until 100% match.")
    public ResponseEntity<AutoImproveResponse> autoImproveResume(
            @RequestBody AutoImproveRequest request) {
        AutoImproveResponse response = jobResumeService.autoImproveResume(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/match-percentages/{resumeId}")
    @Operation(summary = "Get match percentages for each job for a specific resume",
               description = "Retrieve match percentages and job details for all jobs associated with a specific resume")
    public ResponseEntity<List<JobMatchResultDTO>> getMatchPercentagesForResume(
            @Parameter(description = "Resume ID") 
            @PathVariable String resumeId) {
        
        List<JobMatchResultDTO> matchResults = jobResumeService.getMatchPercentagesForResume(resumeId);
        return ResponseEntity.ok(matchResults);
    }

    @GetMapping("/job-seeker/{jobSeekerId}/match-percentages")
    @Operation(summary = "Get match percentages for all resumes of a job seeker",
               description = "Retrieve match percentages and job details for all resumes associated with a specific job seeker")
    public ResponseEntity<List<JobMatchResultDTO>> getMatchPercentagesForJobSeeker(
            @Parameter(description = "Job Seeker ID") 
            @PathVariable String jobSeekerId) {
        
        List<JobMatchResultDTO> matchResults = jobResumeService.getMatchPercentagesForJobSeeker(jobSeekerId);
        return ResponseEntity.ok(matchResults);
    }

    @GetMapping("/job/{googleJobId}/match-percentages")
    @Operation(summary = "Get match percentages for all resumes for a specific job",
               description = "Retrieve match percentages and resume details for all resumes associated with a specific job")
    public ResponseEntity<List<JobMatchResultDTO>> getMatchPercentagesForJob(
            @Parameter(description = "Google Job ID") 
            @PathVariable String googleJobId) {
        
        List<JobMatchResultDTO> matchResults = jobResumeService.getMatchPercentagesForJob(googleJobId);
        return ResponseEntity.ok(matchResults);
    }
} 
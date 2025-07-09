package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobMatchResultDTO;
import com.ensar.jobs.dto.ResumeAnalysisDTO;
import com.ensar.jobs.service.JobResumeService;
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

@RestController
@RequestMapping("/api/resume-analysis")
@RequiredArgsConstructor
@Tag(name = "Resume Analysis", description = "Resume analysis and job matching APIs")
public class JobResumeController {

    private final JobResumeService jobResumeService;

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
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<JobMatchResultDTO> topMatches = jobResumeService.getTopMatchingJobs(resumeText, limit);
        return ResponseEntity.ok(topMatches);
    }

    @GetMapping("/skills")
    @Operation(summary = "Extract skills from resume text", 
               description = "Extract and categorize skills from resume text")
    public ResponseEntity<Map<String, Object>> extractSkills(
            @Parameter(description = "Resume text content") 
            @RequestParam("resumeText") String resumeText) {
        
        // Extract skills using existing service methods
        Set<String> skills = jobResumeService.extractSkillsFromText(resumeText);
        Map<String, Set<String>> skillsByCategory = jobResumeService.extractSkillsByCategory(resumeText);
        
        // Convert to response format
        Map<String, Object> response = new HashMap<>();
        response.put("extractedSkills", new ArrayList<>(skills));
        response.put("skillsByCategory", skillsByCategory.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ArrayList<>(entry.getValue())
            )));
        
        return ResponseEntity.ok(response);
    }
} 
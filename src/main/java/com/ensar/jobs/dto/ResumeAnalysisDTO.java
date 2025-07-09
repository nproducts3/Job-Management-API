package com.ensar.jobs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisDTO {
    private String resumeId;
    private String resumeFile;
    private String resumeText;
    private LocalDateTime uploadedAt;
    private String jobSeekerId;
    private String jobSeekerName;
    
    // Extracted skills from resume
    private List<String> extractedSkills;
    private Map<String, List<String>> skillsByCategory;
    
    // Job match results
    private List<JobMatchResultDTO> jobMatches;
    
    // Summary statistics
    private BigDecimal averageMatchPercentage;
    private Integer totalJobsAnalyzed;
    private Integer highMatchJobs; // >80%
    private Integer mediumMatchJobs; // 50-80%
    private Integer lowMatchJobs; // <50%
} 
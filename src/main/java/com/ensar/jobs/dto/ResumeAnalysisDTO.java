package com.ensar.jobs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for resume analysis results.
 * Contains comprehensive information about the resume analysis including job matches,
 * skill extraction, and pagination details.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisDTO {
    // Resume information
    private String resumeId;
    private String resumeFile;
    private String resumeText;
    private LocalDateTime uploadedAt;
    
    // Job seeker information
    private String jobSeekerId;
    private String jobSeeker;
    private String jobSeekerName;
    
    // Extracted skills from resume
    private List<String> extractedSkills;
    private Map<String, List<String>> skillsByCategory;
    
    // Job match results (paginated)
    private List<JobMatchResultDTO> jobMatches;
    
    // Summary statistics
    private BigDecimal averageMatchPercentage;
    private Integer totalJobsAnalyzed;
    private Integer highMatchJobs;     // >= 70% match
    private Integer mediumMatchJobs;   // 40-69% match
    private Integer lowMatchJobs;      // < 40% match
    
    // Pagination information
    private Integer currentPage;       // 0-based page number
    private Integer totalPages;        // Total number of pages
    private Integer pageSize;          // Number of items per page
    private Boolean hasNextPage;       // Whether there is a next page
    private Boolean hasPreviousPage;   // Whether there is a previous page

    // AI feedback message (for plain string response)
    private String feedback;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
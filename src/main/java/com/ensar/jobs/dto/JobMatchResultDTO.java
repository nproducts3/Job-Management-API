package com.ensar.jobs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchResultDTO {
    private UUID googleJobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private BigDecimal matchPercentage;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private Map<String, BigDecimal> categoryScores;
    private String jobDescription;
    private String qualifications;
    private List<String> responsibilities;
    private List<String> benefits;
    private String applyLink;
    private String salary;
    private String scheduleType;
    private String aiSuggestions;
    private String jobSeekerId;
    private String jobSeekerName;
} 
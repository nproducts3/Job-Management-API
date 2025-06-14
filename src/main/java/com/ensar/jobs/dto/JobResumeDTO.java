package com.ensar.jobs.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResumeDTO {
    private Integer id;
    
    @NotNull(message = "Job ID is required")
    private String jobId;
    
    private String resumeFile;
    private String resumeText;
    
    @DecimalMin(value = "0.0", message = "Match percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Match percentage must not exceed 100")
    private BigDecimal matchPercentage;
    
    private LocalDateTime uploadedAt;
    
    // Additional fields for response
    private String jobTitle;
    private String company;
    private String matchedSkills;
    private String missingSkills;
} 
package com.ensar.jobs.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResumeDTO {
    private String id;
    @NotNull(message = "Google Job ID is required")
    private UUID googleJobId;
    private String resumeFile;
    private String resumeText;
    
    @DecimalMin(value = "0.0", message = "Match percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Match percentage must not exceed 100")
    private BigDecimal matchPercentage;
    
    private LocalDateTime uploadedAt;
    
    // Additional fields for response
    private String jobTitle;
    private String companyName;
    private String matchedSkills;
    private String missingSkills;
    private String jobSeekerId;
} 
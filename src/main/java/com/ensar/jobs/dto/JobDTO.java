package com.ensar.jobs.dto;

import com.ensar.jobs.entity.Job.WorkType;
import com.ensar.jobs.entity.Job.WorkEnvironment;
import com.ensar.jobs.entity.Job.JobStatus;
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
public class JobDTO {
    private String id;

    @NotBlank(message = "Job title is required")
    @Size(max = 200, message = "Job title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String company;

    private String companyId;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    private Integer jobTitleId;

    @NotBlank(message = "Job description is required")
    private String description;

    @Size(max = 100, message = "Salary must not exceed 100 characters")
    private String salary;

    @Min(value = 0, message = "Minimum salary must be non-negative")
    private Integer salaryMin;

    @Min(value = 0, message = "Maximum salary must be non-negative")
    private Integer salaryMax;

    @Size(max = 10, message = "Salary currency must not exceed 10 characters")
    @Builder.Default
    private String salaryCurrency = "INR";

    @Size(max = 50, message = "Experience must not exceed 50 characters")
    private String experience;

    @Min(value = 0, message = "Minimum experience must be non-negative")
    private Integer experienceMin;

    @Min(value = 0, message = "Maximum experience must be non-negative")
    private Integer experienceMax;

    private Integer cityId;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @NotNull(message = "Work type is required")
    private WorkType workType;

    @NotNull(message = "Work environment is required")
    private WorkEnvironment workEnvironment;

    private String skills;
    private String requirements;
    private String benefits;
    private String responsibilities;

    private String applyUrl;

    private LocalDateTime postedDate;
    private LocalDateTime expiryDate;
    @Builder.Default
    private JobStatus status = JobStatus.Active;
    private String categories;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5")
    private BigDecimal rating;

    private String reviews;
    @Builder.Default
    private Integer applicationsCount = 0;
    @Builder.Default
    private Integer viewsCount = 0;
    private String recruiterId;
    @Builder.Default
    private Boolean isUrgent = false;
    @Builder.Default
    private Boolean isFeatured = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
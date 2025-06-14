package com.ensar.jobs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Job Title Data Transfer Object")
public class JobTitleDTO {
    
    @Schema(description = "Unique identifier of the job title", example = "1")
    private Long id;
    
    @Schema(description = "Title of the job position", example = "Software Engineer", required = true)
    @NotBlank(message = "Job title cannot be empty")
    private String title;
    
    @Schema(description = "Timestamp when the job title was created", example = "2024-05-14T01:25:00")
    private LocalDateTime createdDateTime;
    
    @Schema(description = "Timestamp when the job title was last updated", example = "2024-05-14T01:25:00")
    private LocalDateTime lastUpdatedDateTime;
} 
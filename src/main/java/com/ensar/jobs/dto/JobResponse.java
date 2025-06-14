package com.ensar.jobs.dto;

import com.ensar.jobs.entity.City;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private String salaryRange;
    private String companyName;
    private City location;
    private String status;
    private LocalDateTime createdAt;
} 
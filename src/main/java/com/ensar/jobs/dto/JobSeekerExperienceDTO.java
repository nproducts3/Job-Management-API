package com.ensar.jobs.dto;

import lombok.Data;

@Data
public class JobSeekerExperienceDTO {
    private String id;
    private String jobSeekerId;
    private String jobTitle;
    private String companyName;
    private String startDate;
    private String endDate;
    private String responsibilities; // JSON string
} 
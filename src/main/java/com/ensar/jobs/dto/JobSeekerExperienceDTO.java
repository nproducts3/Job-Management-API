package com.ensar.jobs.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobSeekerExperienceDTO {
    private String id;
    private String jobSeekerId;
    private String jobTitle;
    private String companyName;
    private String startDate;
    private String endDate;
    private List<String> responsibilities;
} 
package com.ensar.jobs.dto;

import lombok.Data;

@Data
public class JobSeekerEducationDTO {
    private String id;
    private String jobSeekerId;
    private String degree;
    private String university;
    private Integer graduationYear;
} 
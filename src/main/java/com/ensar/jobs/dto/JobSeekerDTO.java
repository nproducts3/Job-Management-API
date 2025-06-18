package com.ensar.jobs.dto;

import lombok.Data;
import java.util.List;

@Data
public class JobSeekerDTO {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String location;
    private String phone;
    private String desiredSalary;
    private String preferredJobTypes; // JSON string
    private List<JobSeekerSkillDTO> skills;
    private List<JobSeekerExperienceDTO> experiences;
    private List<JobSeekerEducationDTO> educations;
    private List<JobSeekerCertificationDTO> certifications;
} 
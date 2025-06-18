package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerExperienceDTO;
import java.util.List;

public interface JobSeekerExperienceService {
    JobSeekerExperienceDTO createJobSeekerExperience(JobSeekerExperienceDTO dto);
    JobSeekerExperienceDTO updateJobSeekerExperience(String id, JobSeekerExperienceDTO dto);
    JobSeekerExperienceDTO getJobSeekerExperienceById(String id);
    List<JobSeekerExperienceDTO> getAllJobSeekerExperiences();
    void deleteJobSeekerExperience(String id);
} 
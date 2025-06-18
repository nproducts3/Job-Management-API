package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerEducationDTO;
import java.util.List;

public interface JobSeekerEducationService {
    JobSeekerEducationDTO createJobSeekerEducation(JobSeekerEducationDTO dto);
    JobSeekerEducationDTO updateJobSeekerEducation(String id, JobSeekerEducationDTO dto);
    JobSeekerEducationDTO getJobSeekerEducationById(String id);
    List<JobSeekerEducationDTO> getAllJobSeekerEducations();
    void deleteJobSeekerEducation(String id);
} 
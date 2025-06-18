package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerCertificationDTO;
import java.util.List;

public interface JobSeekerCertificationService {
    JobSeekerCertificationDTO createJobSeekerCertification(JobSeekerCertificationDTO dto);
    JobSeekerCertificationDTO updateJobSeekerCertification(String id, JobSeekerCertificationDTO dto);
    JobSeekerCertificationDTO getJobSeekerCertificationById(String id);
    List<JobSeekerCertificationDTO> getAllJobSeekerCertifications();
    void deleteJobSeekerCertification(String id);
} 
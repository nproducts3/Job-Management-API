package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerDTO;
import java.util.List;

public interface JobSeekerService {
    JobSeekerDTO createJobSeeker(JobSeekerDTO jobSeekerDTO);
    JobSeekerDTO updateJobSeeker(String id, JobSeekerDTO jobSeekerDTO);
    JobSeekerDTO getJobSeekerById(String id);
    List<JobSeekerDTO> getAllJobSeekers();
    void deleteJobSeeker(String id);
} 
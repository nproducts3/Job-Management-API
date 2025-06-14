package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobDTO;
import com.ensar.jobs.entity.Job.JobStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface JobService {
    JobDTO createJob(JobDTO jobDTO);
    JobDTO updateJob(String id, JobDTO jobDTO);
    JobDTO getJobById(String id);
    void deleteJob(String id);
    List<JobDTO> getAllJobs();
    List<JobDTO> getJobsByStatus(JobStatus status);
    List<JobDTO> getJobsByCompany(String companyId);
    List<JobDTO> getJobsByCity(Integer cityId);
    List<JobDTO> getFeaturedJobs();
    List<JobDTO> getUrgentJobs();
    List<JobDTO> getJobsByRecruiter(String recruiterId);
    List<JobDTO> searchJobs(String skill, Integer minExperience, Integer maxExperience, Integer cityId, String searchTerm);
    List<JobDTO> getJobsBySalaryRange(Integer minSalary, Integer maxSalary);
    List<JobDTO> getJobsByExperienceRange(Integer minExperience, Integer maxExperience);
    List<JobDTO> getJobsAboutToExpire(LocalDateTime start, LocalDateTime end, JobStatus status);
    List<JobDTO> getRecentlyPostedJobs(LocalDateTime date);
    Long getJobCountByStatus(JobStatus status);
    Long getJobCountByCompany(String companyId);
    List<JobDTO> searchJobsBySkillsExperienceAndLocation(
            String skills, Integer minExperience, Integer maxExperience, Integer cityId);
} 
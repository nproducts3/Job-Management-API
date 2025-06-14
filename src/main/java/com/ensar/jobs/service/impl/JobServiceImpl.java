package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobDTO;
import com.ensar.jobs.entity.Job;
import com.ensar.jobs.entity.Job.JobStatus;
import com.ensar.jobs.repository.JobRepository;
import com.ensar.jobs.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    @Override
    public JobDTO createJob(JobDTO jobDTO) {
        Job job = modelMapper.map(jobDTO, Job.class);
        job = jobRepository.save(job);
        return modelMapper.map(job, JobDTO.class);
    }

    @Override
    public JobDTO updateJob(String id, JobDTO jobDTO) {
        Job existingJob = jobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job not found with id: " + id));
        
        modelMapper.map(jobDTO, existingJob);
        existingJob.setId(id); // Ensure ID is not overwritten
        existingJob = jobRepository.save(existingJob);
        return modelMapper.map(existingJob, JobDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public JobDTO getJobById(String id) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job not found with id: " + id));
        return modelMapper.map(job, JobDTO.class);
    }

    @Override
    public void deleteJob(String id) {
        if (!jobRepository.existsById(id)) {
            throw new EntityNotFoundException("Job not found with id: " + id);
        }
        jobRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsByStatus(JobStatus status) {
        return jobRepository.findByStatus(status).stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsByCompany(String companyId) {
        return jobRepository.findByCompanyEntity_Id(companyId).stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsByCity(Integer cityId) {
        return jobRepository.findByCity_Id(cityId).stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getFeaturedJobs() {
        return jobRepository.findByIsFeaturedTrue().stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getUrgentJobs() {
        return jobRepository.findByIsUrgentTrue().stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsByRecruiter(String recruiterId) {
        return jobRepository.findByRecruiter_Id(recruiterId).stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> searchJobs(String skill, Integer minExperience, Integer maxExperience, Integer cityId, String searchTerm) {
        return jobRepository.searchJobsWithFilters(skill, minExperience, maxExperience, cityId, searchTerm)
                .stream()
                .map(this::mapToJobDTO)
                .collect(Collectors.toList());
    }

    private JobDTO mapToJobDTO(Job job) {
        return JobDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .experienceMin(job.getExperienceMin())
                .experienceMax(job.getExperienceMax())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .cityId(job.getCity().getId())
                .companyId(job.getCompanyEntity().getId())
                .status(job.getStatus())
                .isFeatured(job.getIsFeatured())
                .isUrgent(job.getIsUrgent())
                .postedDate(job.getPostedDate())
                .expiryDate(job.getExpiryDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsBySalaryRange(Integer minSalary, Integer maxSalary) {
        return jobRepository.findBySalaryMinGreaterThanEqualAndSalaryMaxLessThanEqual(minSalary, maxSalary).stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsByExperienceRange(Integer minExperience, Integer maxExperience) {
        return jobRepository.findByExperienceMinGreaterThanEqualAndExperienceMaxLessThanEqual(minExperience, maxExperience).stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsAboutToExpire(LocalDateTime start, LocalDateTime end, JobStatus status) {
        return jobRepository.findByExpiryDateBetweenAndStatus(start, end, status).stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getRecentlyPostedJobs(LocalDateTime date) {
        return jobRepository.findByPostedDateGreaterThanEqual(date).stream()
            .map(job -> modelMapper.map(job, JobDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getJobCountByStatus(JobStatus status) {
        return jobRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getJobCountByCompany(String companyId) {
        return jobRepository.countByCompanyEntity_Id(companyId);
    }

    @Override
    public List<JobDTO> searchJobsBySkillsExperienceAndLocation(
            String skills, Integer minExperience, Integer maxExperience, Integer cityId) {
        List<Job> jobs = jobRepository.findBySkillsExperienceAndLocation(skills, minExperience, maxExperience, cityId);
        return jobs.stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());
    }
} 
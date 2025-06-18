package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerExperienceDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerExperience;
import com.ensar.jobs.repository.JobSeekerExperienceRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.service.JobSeekerExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerExperienceServiceImpl implements JobSeekerExperienceService {

    private final JobSeekerExperienceRepository experienceRepository;
    private final JobSeekerRepository jobSeekerRepository;

    private JobSeekerExperienceDTO convertToDTO(JobSeekerExperience experience) {
        if (experience == null) {
            return null;
        }
        JobSeekerExperienceDTO dto = new JobSeekerExperienceDTO();
        dto.setId(experience.getId());
        dto.setJobTitle(experience.getJobTitle());
        dto.setCompanyName(experience.getCompanyName());
        dto.setStartDate(experience.getStartDate() != null ? experience.getStartDate().toString() : null);
        dto.setEndDate(experience.getEndDate() != null ? experience.getEndDate().toString() : null);
        dto.setResponsibilities(experience.getResponsibilities());
        if (experience.getJobSeeker() != null) {
            dto.setJobSeekerId(experience.getJobSeeker().getId());
        }
        return dto;
    }

    private JobSeekerExperience convertToEntity(JobSeekerExperienceDTO dto) {
        if (dto == null) {
            return null;
        }
        JobSeekerExperience experience = new JobSeekerExperience();
        experience.setId(dto.getId());
        experience.setJobTitle(dto.getJobTitle());
        experience.setCompanyName(dto.getCompanyName());
        experience.setResponsibilities(dto.getResponsibilities());
        if (dto.getStartDate() != null) {
            experience.setStartDate(java.time.LocalDate.parse(dto.getStartDate()));
        }
        if (dto.getEndDate() != null) {
            experience.setEndDate(java.time.LocalDate.parse(dto.getEndDate()));
        }
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            experience.setJobSeeker(jobSeeker);
        }
        return experience;
    }

    @Override
    @Transactional
    public List<JobSeekerExperienceDTO> getAllJobSeekerExperiences() {
        return experienceRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobSeekerExperienceDTO getJobSeekerExperienceById(String id) {
        JobSeekerExperience experience = experienceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker experience not found"));
        return convertToDTO(experience);
    }

    @Override
    @Transactional
    public JobSeekerExperienceDTO createJobSeekerExperience(JobSeekerExperienceDTO dto) {
        JobSeekerExperience entity = convertToEntity(dto);
        return convertToDTO(experienceRepository.save(entity));
    }

    @Override
    @Transactional
    public JobSeekerExperienceDTO updateJobSeekerExperience(String id, JobSeekerExperienceDTO dto) {
        JobSeekerExperience existingExperience = experienceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker experience not found"));
        existingExperience.setJobTitle(dto.getJobTitle());
        existingExperience.setCompanyName(dto.getCompanyName());
        existingExperience.setResponsibilities(dto.getResponsibilities());
        if (dto.getStartDate() != null) {
            existingExperience.setStartDate(java.time.LocalDate.parse(dto.getStartDate()));
        }
        if (dto.getEndDate() != null) {
            existingExperience.setEndDate(java.time.LocalDate.parse(dto.getEndDate()));
        }
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            existingExperience.setJobSeeker(jobSeeker);
        }
        return convertToDTO(experienceRepository.save(existingExperience));
    }

    @Override
    @Transactional
    public void deleteJobSeekerExperience(String id) {
        experienceRepository.deleteById(id);
    }
} 
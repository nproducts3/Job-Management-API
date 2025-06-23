package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerExperienceDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerExperience;
import com.ensar.jobs.repository.JobSeekerExperienceRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerExperienceService {

    private final JobSeekerExperienceRepository experienceRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public JobSeekerExperienceDTO createJobSeekerExperience(JobSeekerExperienceDTO dto) {
        JobSeekerExperience experience = mapToEntity(dto);
        
        // Set the JobSeeker entity from jobSeekerId
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
            experience.setJobSeeker(jobSeeker);
        }
        
        experience = experienceRepository.save(experience);
        return mapToDTO(experience);
    }

    public JobSeekerExperienceDTO updateJobSeekerExperience(String id, JobSeekerExperienceDTO dto) {
        JobSeekerExperience existingExperience = experienceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker experience not found with id: " + id));
        
        updateEntityFromDTO(existingExperience, dto);
        existingExperience.setId(id); // Ensure ID is not overwritten
        
        // Set the JobSeeker entity from jobSeekerId if provided
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
            existingExperience.setJobSeeker(jobSeeker);
        }
        
        existingExperience = experienceRepository.save(existingExperience);
        return mapToDTO(existingExperience);
    }

    @Transactional(readOnly = true)
    public JobSeekerExperienceDTO getJobSeekerExperienceById(String id) {
        JobSeekerExperience experience = experienceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker experience not found with id: " + id));
        return mapToDTO(experience);
    }

    @Transactional(readOnly = true)
    public List<JobSeekerExperienceDTO> getAllJobSeekerExperiences() {
        return experienceRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerExperience(String id) {
        if (!experienceRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker experience not found with id: " + id);
        }
        experienceRepository.deleteById(id);
    }

    // Manual mapping methods
    private JobSeekerExperience mapToEntity(JobSeekerExperienceDTO dto) {
        JobSeekerExperience entity = new JobSeekerExperience();
        entity.setId(dto.getId());
        entity.setJobTitle(dto.getJobTitle());
        entity.setCompanyName(dto.getCompanyName());
        entity.setStartDate(dto.getStartDate() != null ? LocalDate.parse(dto.getStartDate()) : null);
        entity.setEndDate(dto.getEndDate() != null ? LocalDate.parse(dto.getEndDate()) : null);
        entity.setResponsibilities(dto.getResponsibilities());
        return entity;
    }

    private JobSeekerExperienceDTO mapToDTO(JobSeekerExperience entity) {
        JobSeekerExperienceDTO dto = new JobSeekerExperienceDTO();
        dto.setId(entity.getId());
        dto.setJobSeekerId(entity.getJobSeeker() != null ? entity.getJobSeeker().getId() : null);
        dto.setJobTitle(entity.getJobTitle());
        dto.setCompanyName(entity.getCompanyName());
        dto.setStartDate(entity.getStartDate() != null ? entity.getStartDate().toString() : null);
        dto.setEndDate(entity.getEndDate() != null ? entity.getEndDate().toString() : null);
        dto.setResponsibilities(entity.getResponsibilities());
        return dto;
    }

    private void updateEntityFromDTO(JobSeekerExperience entity, JobSeekerExperienceDTO dto) {
        if (dto.getJobTitle() != null) entity.setJobTitle(dto.getJobTitle());
        if (dto.getCompanyName() != null) entity.setCompanyName(dto.getCompanyName());
        if (dto.getStartDate() != null) entity.setStartDate(LocalDate.parse(dto.getStartDate()));
        if (dto.getEndDate() != null) entity.setEndDate(LocalDate.parse(dto.getEndDate()));
        if (dto.getResponsibilities() != null) entity.setResponsibilities(dto.getResponsibilities());
    }
} 
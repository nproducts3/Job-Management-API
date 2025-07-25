package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerEducationDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerEducation;
import com.ensar.jobs.repository.JobSeekerEducationRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerEducationService {

    private final JobSeekerEducationRepository educationRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public JobSeekerEducationDTO createJobSeekerEducation(JobSeekerEducationDTO dto) {
        JobSeekerEducation education = mapToEntity(dto);
        
        // Set the JobSeeker entity from jobSeekerId
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
            education.setJobSeeker(jobSeeker);
        }
        
        education = educationRepository.save(education);
        return mapToDTO(education);
    }

    public JobSeekerEducationDTO updateJobSeekerEducation(String id, JobSeekerEducationDTO dto) {
        JobSeekerEducation existingEducation = educationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker education not found with id: " + id));
        
        updateEntityFromDTO(existingEducation, dto);
        existingEducation.setId(id); // Ensure ID is not overwritten
        
        // Set the JobSeeker entity from jobSeekerId if provided
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
            existingEducation.setJobSeeker(jobSeeker);
        }
        
        existingEducation = educationRepository.save(existingEducation);
        return mapToDTO(existingEducation);
    }

    @Transactional(readOnly = true)
    public JobSeekerEducationDTO getJobSeekerEducationById(String id) {
        JobSeekerEducation education = educationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker education not found with id: " + id));
        return mapToDTO(education);
    }

    @Transactional(readOnly = true)
    public List<JobSeekerEducationDTO> getAllJobSeekerEducations() {
        return educationRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobSeekerEducationDTO> getAllByJobSeekerEducationId(String jobSeekerId) {
        return educationRepository.findByJobSeekerId(jobSeekerId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerEducation(String id) {
        if (!educationRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker education not found with id: " + id);
        }
        educationRepository.deleteById(id);
    }

    // Manual mapping methods
    private JobSeekerEducation mapToEntity(JobSeekerEducationDTO dto) {
        JobSeekerEducation entity = new JobSeekerEducation();
        entity.setId(dto.getId());
        entity.setDegree(dto.getDegree());
        entity.setUniversity(dto.getUniversity());
        entity.setGraduationYear(dto.getGraduationYear());
        return entity;
    }

    private JobSeekerEducationDTO mapToDTO(JobSeekerEducation entity) {
        JobSeekerEducationDTO dto = new JobSeekerEducationDTO();
        dto.setId(entity.getId());
        dto.setJobSeekerId(entity.getJobSeeker() != null ? entity.getJobSeeker().getId() : null);
        dto.setDegree(entity.getDegree());
        dto.setUniversity(entity.getUniversity());
        dto.setGraduationYear(entity.getGraduationYear());
        return dto;
    }

    private void updateEntityFromDTO(JobSeekerEducation entity, JobSeekerEducationDTO dto) {
        if (dto.getDegree() != null) entity.setDegree(dto.getDegree());
        if (dto.getUniversity() != null) entity.setUniversity(dto.getUniversity());
        if (dto.getGraduationYear() != null) entity.setGraduationYear(dto.getGraduationYear());
    }
} 
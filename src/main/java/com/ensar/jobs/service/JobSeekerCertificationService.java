package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerCertificationDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerCertification;
import com.ensar.jobs.repository.JobSeekerCertificationRepository;
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
public class JobSeekerCertificationService {

    private final JobSeekerCertificationRepository certificationRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public JobSeekerCertificationDTO createJobSeekerCertification(JobSeekerCertificationDTO dto) {
        JobSeekerCertification certification = mapToEntity(dto);
        
        // Set the JobSeeker entity from jobSeekerId
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
            certification.setJobSeeker(jobSeeker);
        }
        
        certification = certificationRepository.save(certification);
        return mapToDTO(certification);
    }

    public JobSeekerCertificationDTO updateJobSeekerCertification(String id, JobSeekerCertificationDTO dto) {
        JobSeekerCertification existingCertification = certificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker certification not found with id: " + id));
        
        updateEntityFromDTO(existingCertification, dto);
        existingCertification.setId(id); // Ensure ID is not overwritten
        
        // Set the JobSeeker entity from jobSeekerId if provided
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
            existingCertification.setJobSeeker(jobSeeker);
        }
        
        existingCertification = certificationRepository.save(existingCertification);
        return mapToDTO(existingCertification);
    }

    @Transactional(readOnly = true)
    public JobSeekerCertificationDTO getJobSeekerCertificationById(String id) {
        JobSeekerCertification certification = certificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker certification not found with id: " + id));
        return mapToDTO(certification);
    }

    @Transactional(readOnly = true)
    public List<JobSeekerCertificationDTO> getAllJobSeekerCertifications() {
        return certificationRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerCertification(String id) {
        if (!certificationRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker certification not found with id: " + id);
        }
        certificationRepository.deleteById(id);
    }

    // Manual mapping methods
    private JobSeekerCertification mapToEntity(JobSeekerCertificationDTO dto) {
        JobSeekerCertification entity = new JobSeekerCertification();
        entity.setId(dto.getId());
        entity.setCertificationName(dto.getCertificationName());
        return entity;
    }

    private JobSeekerCertificationDTO mapToDTO(JobSeekerCertification entity) {
        JobSeekerCertificationDTO dto = new JobSeekerCertificationDTO();
        dto.setId(entity.getId());
        dto.setJobSeekerId(entity.getJobSeeker() != null ? entity.getJobSeeker().getId() : null);
        dto.setCertificationName(entity.getCertificationName());
        return dto;
    }

    private void updateEntityFromDTO(JobSeekerCertification entity, JobSeekerCertificationDTO dto) {
        if (dto.getCertificationName() != null) entity.setCertificationName(dto.getCertificationName());
    }
} 
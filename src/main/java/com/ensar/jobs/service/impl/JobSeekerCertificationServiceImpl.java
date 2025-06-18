package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerCertificationDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerCertification;
import com.ensar.jobs.repository.JobSeekerCertificationRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.service.JobSeekerCertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerCertificationServiceImpl implements JobSeekerCertificationService {

    private final JobSeekerCertificationRepository certificationRepository;
    private final JobSeekerRepository jobSeekerRepository;

    private JobSeekerCertificationDTO convertToDTO(JobSeekerCertification certification) {
        if (certification == null) {
            return null;
        }
        
        JobSeekerCertificationDTO dto = new JobSeekerCertificationDTO();
        dto.setId(certification.getId());
        dto.setCertificationName(certification.getCertificationName());
        
        if (certification.getJobSeeker() != null) {
            dto.setJobSeekerId(certification.getJobSeeker().getId());
        }
        
        return dto;
    }

    private JobSeekerCertification convertToEntity(JobSeekerCertificationDTO dto) {
        if (dto == null) {
            return null;
        }
        
        JobSeekerCertification certification = new JobSeekerCertification();
        certification.setId(dto.getId());
        certification.setCertificationName(dto.getCertificationName());
        
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            certification.setJobSeeker(jobSeeker);
        }
        
        return certification;
    }

    @Override
    @Transactional
    public List<JobSeekerCertificationDTO> getAllJobSeekerCertifications() {
        return certificationRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobSeekerCertificationDTO getJobSeekerCertificationById(String id) {
        JobSeekerCertification certification = certificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker certification not found"));
        return convertToDTO(certification);
    }

    @Override
    @Transactional
    public JobSeekerCertificationDTO createJobSeekerCertification(JobSeekerCertificationDTO dto) {
        JobSeekerCertification entity = convertToEntity(dto);
        return convertToDTO(certificationRepository.save(entity));
    }

    @Override
    @Transactional
    public JobSeekerCertificationDTO updateJobSeekerCertification(String id, JobSeekerCertificationDTO dto) {
        JobSeekerCertification existingCertification = certificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker certification not found"));
        
        existingCertification.setCertificationName(dto.getCertificationName());
        
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            existingCertification.setJobSeeker(jobSeeker);
        }
        
        return convertToDTO(certificationRepository.save(existingCertification));
    }

    @Override
    @Transactional
    public void deleteJobSeekerCertification(String id) {
        certificationRepository.deleteById(id);
    }
} 
package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerCertificationDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerCertification;
import com.ensar.jobs.repository.JobSeekerCertificationRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobSeekerCertificationService {

    private final JobSeekerCertificationRepository certificationRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final String UPLOAD_DIR = "uploads/certifications";

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

    public JobSeekerCertificationDTO createJobSeekerCertificationWithFile(MultipartFile file, JobSeekerCertificationDTO dto) throws IOException {
        // Validate job seeker
        if (dto.getJobSeekerId() == null) {
            throw new IllegalArgumentException("jobSeekerId is required");
        }
        
        JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
            .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
        
        // Save the certification file
        String filename = saveCertificationFile(file, jobSeeker, dto.getCertificationName());
        
        // Create certification entity
        JobSeekerCertification certification = mapToEntity(dto);
        certification.setJobSeeker(jobSeeker);
        certification.setCertificationFile(filename);
        
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

    public JobSeekerCertificationDTO updateJobSeekerCertificationWithFile(String id, MultipartFile file, JobSeekerCertificationDTO dto) throws IOException {
        JobSeekerCertification existingCertification = certificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker certification not found with id: " + id));
        
        // Delete old file if exists
        if (existingCertification.getCertificationFile() != null) {
            deleteCertificationFile(existingCertification.getCertificationFile());
        }
        
        // Save new file
        JobSeeker jobSeeker = existingCertification.getJobSeeker();
        String filename = saveCertificationFile(file, jobSeeker, dto.getCertificationName());
        
        // Update entity
        updateEntityFromDTO(existingCertification, dto);
        existingCertification.setCertificationFile(filename);
        existingCertification.setId(id); // Ensure ID is not overwritten
        
        // Set the JobSeeker entity from jobSeekerId if provided
        if (dto.getJobSeekerId() != null) {
            jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
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

    @Transactional(readOnly = true)
    public List<JobSeekerCertificationDTO> getCertificationsByJobSeekerId(String jobSeekerId) {
        return certificationRepository.findByJobSeekerId(jobSeekerId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerCertification(String id) {
        JobSeekerCertification certification = certificationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker certification not found with id: " + id));
        
        // Delete the file if exists
        if (certification.getCertificationFile() != null) {
            deleteCertificationFile(certification.getCertificationFile());
        }
        
        certificationRepository.deleteById(id);
    }

    private String saveCertificationFile(MultipartFile file, JobSeeker jobSeeker, String certificationName) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);
        
        String originalName = file.getOriginalFilename();
        String safeFirstName = jobSeeker.getFirstName().replaceAll("[^a-zA-Z0-9]", "_");
        String safeLastName = jobSeeker.getLastName().replaceAll("[^a-zA-Z0-9]", "_");
        String safeCertificationName = (certificationName != null) ? 
            certificationName.replaceAll("[^a-zA-Z0-9]", "_") : "certification";
        
        // Create filename with certificate name and user name
        String filename = safeFirstName + "_" + safeLastName + "_" + safeCertificationName + "_" + UUID.randomUUID() + "_" + originalName;
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        log.info("Certification file saved: {}", filename);
        return filename;
    }

    private void deleteCertificationFile(String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            Files.deleteIfExists(filePath);
            log.info("Certification file deleted: {}", filename);
        } catch (IOException e) {
            log.warn("Failed to delete certification file: {}", filename, e);
        }
    }

    // Manual mapping methods
    private JobSeekerCertification mapToEntity(JobSeekerCertificationDTO dto) {
        JobSeekerCertification entity = new JobSeekerCertification();
        entity.setId(dto.getId());
        entity.setCertificationName(dto.getCertificationName());
        entity.setCertificationFile(dto.getCertificationFile());
        entity.setIssuedDate(dto.getIssuedDate());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setIssuingOrganization(dto.getIssuingOrganization());
        return entity;
    }

    private JobSeekerCertificationDTO mapToDTO(JobSeekerCertification entity) {
        JobSeekerCertificationDTO dto = new JobSeekerCertificationDTO();
        dto.setId(entity.getId());
        dto.setJobSeekerId(entity.getJobSeeker() != null ? entity.getJobSeeker().getId() : null);
        dto.setCertificationName(entity.getCertificationName());
        dto.setCertificationFile(entity.getCertificationFile());
        dto.setIssuedDate(entity.getIssuedDate());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setIssuingOrganization(entity.getIssuingOrganization());
        return dto;
    }

    private void updateEntityFromDTO(JobSeekerCertification entity, JobSeekerCertificationDTO dto) {
        if (dto.getCertificationName() != null) entity.setCertificationName(dto.getCertificationName());
        if (dto.getCertificationFile() != null) entity.setCertificationFile(dto.getCertificationFile());
        if (dto.getIssuedDate() != null) entity.setIssuedDate(dto.getIssuedDate());
        if (dto.getExpiryDate() != null) entity.setExpiryDate(dto.getExpiryDate());
        if (dto.getIssuingOrganization() != null) entity.setIssuingOrganization(dto.getIssuingOrganization());
    }
} 
package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.User;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerService {

    private static final Logger logger = LoggerFactory.getLogger(JobSeekerService.class);

    private final JobSeekerRepository jobSeekerRepository;
    private final UserRepository userRepository;

    public JobSeekerDTO createJobSeeker(JobSeekerDTO jobSeekerDTO) {
        JobSeeker jobSeeker = mapToEntity(jobSeekerDTO);
        // Assign a UUID if id is null or empty
        if (jobSeeker.getId() == null || jobSeeker.getId().isEmpty()) {
            jobSeeker.setId(java.util.UUID.randomUUID().toString());
        }
        
        // Set the User entity from userId
        if (jobSeekerDTO.getUserId() != null) {
            User user = userRepository.findById(jobSeekerDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + jobSeekerDTO.getUserId()));
            jobSeeker.setUser(user);
        }
        
        jobSeeker = jobSeekerRepository.save(jobSeeker);
        return mapToDTO(jobSeeker);
    }

    public JobSeekerDTO updateJobSeeker(String id, JobSeekerDTO jobSeekerDTO) {
        JobSeeker existingJobSeeker = jobSeekerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + id));
        
        updateEntityFromDTO(existingJobSeeker, jobSeekerDTO);
        existingJobSeeker.setId(id); // Ensure ID is not overwritten
        
        // Set the User entity from userId if provided
        if (jobSeekerDTO.getUserId() != null) {
            User user = userRepository.findById(jobSeekerDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + jobSeekerDTO.getUserId()));
            existingJobSeeker.setUser(user);
        }
        
        existingJobSeeker = jobSeekerRepository.save(existingJobSeeker);
        return mapToDTO(existingJobSeeker);
    }

    @Transactional(readOnly = true)
    public JobSeekerDTO getJobSeekerById(String id) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + id));
        try {
            org.hibernate.Hibernate.initialize(jobSeeker.getUser());
            logger.info("JobSeeker id {} user class: {}", jobSeeker.getId(), jobSeeker.getUser() != null ? jobSeeker.getUser().getClass().getName() : "null");
            return mapToDTO(jobSeeker);
        } catch (Exception e) {
            logger.error("Failed to map JobSeeker with id {}: {}", jobSeeker.getId(), e.getMessage());
            throw new RuntimeException("Failed to map JobSeeker: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<JobSeekerDTO> getAllJobSeekers() {
        return jobSeekerRepository.findAll().stream()
            .filter(jobSeeker -> {
                Object user = jobSeeker.getUser();
                if (user == null) {
                    logger.warn("JobSeeker with id {} has null user and will be skipped.", jobSeeker.getId());
                    return false;
                }
                logger.info("JobSeeker id {} user class: {}", jobSeeker.getId(), user.getClass().getName());
                return true;
            })
            .map(jobSeeker -> {
                try {
                    Hibernate.initialize(jobSeeker.getUser());
                    return mapToDTO(jobSeeker);
                } catch (Exception e) {
                    logger.error("Failed to map JobSeeker with id {}: {}", jobSeeker.getId(), e.getMessage());
                    return null;
                }
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
    }

    public void deleteJobSeeker(String id) {
        if (!jobSeekerRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker not found with id: " + id);
        }
        jobSeekerRepository.deleteById(id);
    }

    // Manual mapping methods
    private JobSeeker mapToEntity(JobSeekerDTO dto) {
        JobSeeker entity = new JobSeeker();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setLocation(dto.getLocation());
        entity.setPhone(dto.getPhone());
        entity.setDesiredSalary(dto.getDesiredSalary());
        entity.setPreferredJobTypes(dto.getPreferredJobTypes());
        return entity;
    }

    private JobSeekerDTO mapToDTO(JobSeeker entity) {
        JobSeekerDTO dto = new JobSeekerDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setLocation(entity.getLocation());
        dto.setPhone(entity.getPhone());
        dto.setDesiredSalary(entity.getDesiredSalary());
        dto.setPreferredJobTypes(entity.getPreferredJobTypes());
        return dto;
    }

    private void updateEntityFromDTO(JobSeeker entity, JobSeekerDTO dto) {
        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getDesiredSalary() != null) entity.setDesiredSalary(dto.getDesiredSalary());
        if (dto.getPreferredJobTypes() != null) entity.setPreferredJobTypes(dto.getPreferredJobTypes());
    }
} 
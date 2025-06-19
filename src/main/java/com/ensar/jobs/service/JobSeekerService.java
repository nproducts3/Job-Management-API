package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.repository.JobSeekerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public JobSeekerDTO createJobSeeker(JobSeekerDTO jobSeekerDTO) {
        JobSeeker jobSeeker = modelMapper.map(jobSeekerDTO, JobSeeker.class);
        jobSeeker = jobSeekerRepository.save(jobSeeker);
        return modelMapper.map(jobSeeker, JobSeekerDTO.class);
    }

    public JobSeekerDTO updateJobSeeker(String id, JobSeekerDTO jobSeekerDTO) {
        JobSeeker existingJobSeeker = jobSeekerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + id));
        
        modelMapper.map(jobSeekerDTO, existingJobSeeker);
        existingJobSeeker.setId(id); // Ensure ID is not overwritten
        existingJobSeeker = jobSeekerRepository.save(existingJobSeeker);
        return modelMapper.map(existingJobSeeker, JobSeekerDTO.class);
    }

    @Transactional(readOnly = true)
    public JobSeekerDTO getJobSeekerById(String id) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + id));
        try {
            org.hibernate.Hibernate.initialize(jobSeeker.getUser());
            logger.info("JobSeeker id {} user class: {}", jobSeeker.getId(), jobSeeker.getUser() != null ? jobSeeker.getUser().getClass().getName() : "null");
            return modelMapper.map(jobSeeker, JobSeekerDTO.class);
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
                    return modelMapper.map(jobSeeker, JobSeekerDTO.class);
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
} 
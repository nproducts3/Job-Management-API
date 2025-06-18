package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.User;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.repository.UserRepository;
import com.ensar.jobs.service.JobSeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobSeekerServiceImpl implements JobSeekerService {
    private final JobSeekerRepository jobSeekerRepository;
    private final UserRepository userRepository;

    private JobSeekerDTO convertToDTO(JobSeeker jobSeeker) {
        if (jobSeeker == null) return null;
        JobSeekerDTO dto = new JobSeekerDTO();
        dto.setId(jobSeeker.getId());
        dto.setUserId(jobSeeker.getUser() != null ? jobSeeker.getUser().getId() : null);
        dto.setFirstName(jobSeeker.getFirstName());
        dto.setLastName(jobSeeker.getLastName());
        dto.setLocation(jobSeeker.getLocation());
        dto.setPhone(jobSeeker.getPhone());
        dto.setDesiredSalary(jobSeeker.getDesiredSalary());
        dto.setPreferredJobTypes(jobSeeker.getPreferredJobTypes());
        return dto;
    }

    private JobSeeker convertToEntity(JobSeekerDTO dto) {
        if (dto == null) return null;
        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setId(dto.getId());
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            jobSeeker.setUser(user);
        }
        jobSeeker.setFirstName(dto.getFirstName());
        jobSeeker.setLastName(dto.getLastName());
        jobSeeker.setLocation(dto.getLocation());
        jobSeeker.setPhone(dto.getPhone());
        jobSeeker.setDesiredSalary(dto.getDesiredSalary());
        jobSeeker.setPreferredJobTypes(dto.getPreferredJobTypes());
        return jobSeeker;
    }

    @Override
    @Transactional
    public JobSeekerDTO createJobSeeker(JobSeekerDTO dto) {
        JobSeeker entity = convertToEntity(dto);
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        return convertToDTO(jobSeekerRepository.save(entity));
    }

    @Override
    @Transactional
    public JobSeekerDTO updateJobSeeker(String id, JobSeekerDTO dto) {
        JobSeeker entity = jobSeekerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker not found"));
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            entity.setUser(user);
        }
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setLocation(dto.getLocation());
        entity.setPhone(dto.getPhone());
        entity.setDesiredSalary(dto.getDesiredSalary());
        entity.setPreferredJobTypes(dto.getPreferredJobTypes());
        return convertToDTO(jobSeekerRepository.save(entity));
    }

    @Override
    public JobSeekerDTO getJobSeekerById(String id) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker not found"));
        return convertToDTO(jobSeeker);
    }

    @Override
    public List<JobSeekerDTO> getAllJobSeekers() {
        return jobSeekerRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteJobSeeker(String id) {
        jobSeekerRepository.deleteById(id);
    }
} 
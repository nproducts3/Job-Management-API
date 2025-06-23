package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerSkillDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerSkill;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.repository.JobSeekerSkillRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerSkillService {

    private final JobSeekerSkillRepository skillRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public JobSeekerSkillDTO createJobSeekerSkill(JobSeekerSkillDTO dto) {
        JobSeekerSkill skill = mapToEntity(dto);
        
        // Set the JobSeeker entity from jobSeekerId
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
            skill.setJobSeeker(jobSeeker);
        }
        
        skill = skillRepository.save(skill);
        return mapToDTO(skill);
    }

    public JobSeekerSkillDTO updateJobSeekerSkill(String id, JobSeekerSkillDTO dto) {
        JobSeekerSkill existingSkill = skillRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker skill not found with id: " + id));
        
        updateEntityFromDTO(existingSkill, dto);
        existingSkill.setId(id); // Ensure ID is not overwritten
        
        // Set the JobSeeker entity from jobSeekerId if provided
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
            existingSkill.setJobSeeker(jobSeeker);
        }
        
        existingSkill = skillRepository.save(existingSkill);
        return mapToDTO(existingSkill);
    }

    @Transactional(readOnly = true)
    public JobSeekerSkillDTO getJobSeekerSkillById(String id) {
        JobSeekerSkill skill = skillRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker skill not found with id: " + id));
        return mapToDTO(skill);
    }

    @Transactional(readOnly = true)
    public List<JobSeekerSkillDTO> getAllJobSeekerSkills() {
        return skillRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerSkill(String id) {
        if (!skillRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }

    // Manual mapping methods
    private JobSeekerSkill mapToEntity(JobSeekerSkillDTO dto) {
        JobSeekerSkill entity = new JobSeekerSkill();
        entity.setId(dto.getId());
        entity.setSkillName(dto.getSkillName());
        return entity;
    }

    private JobSeekerSkillDTO mapToDTO(JobSeekerSkill entity) {
        JobSeekerSkillDTO dto = new JobSeekerSkillDTO();
        dto.setId(entity.getId());
        dto.setJobSeekerId(entity.getJobSeeker() != null ? entity.getJobSeeker().getId() : null);
        dto.setSkillName(entity.getSkillName());
        return dto;
    }

    private void updateEntityFromDTO(JobSeekerSkill entity, JobSeekerSkillDTO dto) {
        if (dto.getSkillName() != null) entity.setSkillName(dto.getSkillName());
    }
} 
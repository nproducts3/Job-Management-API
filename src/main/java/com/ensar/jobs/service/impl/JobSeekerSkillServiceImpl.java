package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerSkillDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerSkill;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.repository.JobSeekerSkillRepository;
import com.ensar.jobs.service.JobSeekerSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerSkillServiceImpl implements JobSeekerSkillService {

    private final JobSeekerSkillRepository jobSeekerSkillRepository;
    private final JobSeekerRepository jobSeekerRepository;

    private JobSeekerSkillDTO convertToDTO(JobSeekerSkill skill) {
        if (skill == null) {
            return null;
        }
        
        JobSeekerSkillDTO dto = new JobSeekerSkillDTO();
        dto.setId(skill.getId());
        dto.setSkillName(skill.getSkillName());
        
        if (skill.getJobSeeker() != null) {
            dto.setJobSeekerId(skill.getJobSeeker().getId());
        }
        
        return dto;
    }

    private JobSeekerSkill convertToEntity(JobSeekerSkillDTO dto) {
        if (dto == null) {
            return null;
        }
        
        JobSeekerSkill skill = new JobSeekerSkill();
        skill.setId(dto.getId());
        skill.setSkillName(dto.getSkillName());
        
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            skill.setJobSeeker(jobSeeker);
        }
        
        return skill;
    }

    @Override
    @Transactional
    public List<JobSeekerSkillDTO> getAllJobSeekerSkills() {
        return jobSeekerSkillRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobSeekerSkillDTO getJobSeekerSkillById(String id) {
        JobSeekerSkill skill = jobSeekerSkillRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker skill not found"));
        return convertToDTO(skill);
    }

    @Override
    @Transactional
    public JobSeekerSkillDTO createJobSeekerSkill(JobSeekerSkillDTO dto) {
        JobSeekerSkill entity = convertToEntity(dto);
        return convertToDTO(jobSeekerSkillRepository.save(entity));
    }

    @Override
    @Transactional
    public JobSeekerSkillDTO updateJobSeekerSkill(String id, JobSeekerSkillDTO dto) {
        JobSeekerSkill existingSkill = jobSeekerSkillRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job seeker skill not found"));
        
        existingSkill.setSkillName(dto.getSkillName());
        
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            existingSkill.setJobSeeker(jobSeeker);
        }
        
        return convertToDTO(jobSeekerSkillRepository.save(existingSkill));
    }

    @Override
    @Transactional
    public void deleteJobSeekerSkill(String id) {
        jobSeekerSkillRepository.deleteById(id);
    }
} 
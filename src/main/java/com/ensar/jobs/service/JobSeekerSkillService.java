package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerSkillDTO;
import com.ensar.jobs.entity.JobSeekerSkill;
import com.ensar.jobs.repository.JobSeekerSkillRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerSkillService {

    private final JobSeekerSkillRepository skillRepository;
    private final ModelMapper modelMapper;

    public JobSeekerSkillDTO createJobSeekerSkill(JobSeekerSkillDTO dto) {
        JobSeekerSkill skill = modelMapper.map(dto, JobSeekerSkill.class);
        skill = skillRepository.save(skill);
        return modelMapper.map(skill, JobSeekerSkillDTO.class);
    }

    public JobSeekerSkillDTO updateJobSeekerSkill(String id, JobSeekerSkillDTO dto) {
        JobSeekerSkill existingSkill = skillRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker skill not found with id: " + id));
        
        modelMapper.map(dto, existingSkill);
        existingSkill.setId(id); // Ensure ID is not overwritten
        existingSkill = skillRepository.save(existingSkill);
        return modelMapper.map(existingSkill, JobSeekerSkillDTO.class);
    }

    @Transactional(readOnly = true)
    public JobSeekerSkillDTO getJobSeekerSkillById(String id) {
        JobSeekerSkill skill = skillRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker skill not found with id: " + id));
        return modelMapper.map(skill, JobSeekerSkillDTO.class);
    }

    @Transactional(readOnly = true)
    public List<JobSeekerSkillDTO> getAllJobSeekerSkills() {
        return skillRepository.findAll().stream()
            .map(skill -> modelMapper.map(skill, JobSeekerSkillDTO.class))
            .collect(Collectors.toList());
    }

    public void deleteJobSeekerSkill(String id) {
        if (!skillRepository.existsById(id)) {
            throw new EntityNotFoundException("Job seeker skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }
} 
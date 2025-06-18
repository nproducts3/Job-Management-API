package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerSkillDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerSkill;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.repository.JobSeekerSkillRepository;
import com.ensar.jobs.service.JobSeekerSkillService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobSeekerSkillServiceImpl implements JobSeekerSkillService {
    @Autowired
    private JobSeekerSkillRepository skillRepository;
    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public JobSeekerSkillDTO createJobSeekerSkill(JobSeekerSkillDTO dto) {
        JobSeekerSkill entity = modelMapper.map(dto, JobSeekerSkill.class);
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId()).orElseThrow();
            entity.setJobSeeker(jobSeeker);
        }
        return modelMapper.map(skillRepository.save(entity), JobSeekerSkillDTO.class);
    }

    @Override
    public JobSeekerSkillDTO updateJobSeekerSkill(String id, JobSeekerSkillDTO dto) {
        JobSeekerSkill entity = skillRepository.findById(id).orElseThrow();
        modelMapper.map(dto, entity);
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId()).orElseThrow();
            entity.setJobSeeker(jobSeeker);
        }
        return modelMapper.map(skillRepository.save(entity), JobSeekerSkillDTO.class);
    }

    @Override
    public JobSeekerSkillDTO getJobSeekerSkillById(String id) {
        return skillRepository.findById(id)
            .map(entity -> modelMapper.map(entity, JobSeekerSkillDTO.class))
            .orElse(null);
    }

    @Override
    public List<JobSeekerSkillDTO> getAllJobSeekerSkills() {
        return skillRepository.findAll().stream()
            .map(entity -> modelMapper.map(entity, JobSeekerSkillDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteJobSeekerSkill(String id) {
        skillRepository.deleteById(id);
    }
} 
package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerEducationDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerEducation;
import com.ensar.jobs.repository.JobSeekerEducationRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.service.JobSeekerEducationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobSeekerEducationServiceImpl implements JobSeekerEducationService {
    @Autowired
    private JobSeekerEducationRepository educationRepository;
    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public JobSeekerEducationDTO createJobSeekerEducation(JobSeekerEducationDTO dto) {
        JobSeekerEducation entity = modelMapper.map(dto, JobSeekerEducation.class);
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId()).orElseThrow();
            entity.setJobSeeker(jobSeeker);
        }
        return modelMapper.map(educationRepository.save(entity), JobSeekerEducationDTO.class);
    }

    @Override
    public JobSeekerEducationDTO updateJobSeekerEducation(String id, JobSeekerEducationDTO dto) {
        JobSeekerEducation entity = educationRepository.findById(id).orElseThrow();
        modelMapper.map(dto, entity);
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId()).orElseThrow();
            entity.setJobSeeker(jobSeeker);
        }
        return modelMapper.map(educationRepository.save(entity), JobSeekerEducationDTO.class);
    }

    @Override
    public JobSeekerEducationDTO getJobSeekerEducationById(String id) {
        return educationRepository.findById(id)
            .map(entity -> modelMapper.map(entity, JobSeekerEducationDTO.class))
            .orElse(null);
    }

    @Override
    public List<JobSeekerEducationDTO> getAllJobSeekerEducations() {
        return educationRepository.findAll().stream()
            .map(entity -> modelMapper.map(entity, JobSeekerEducationDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteJobSeekerEducation(String id) {
        educationRepository.deleteById(id);
    }
} 
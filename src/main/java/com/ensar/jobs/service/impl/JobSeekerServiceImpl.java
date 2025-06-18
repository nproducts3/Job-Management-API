package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.User;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.repository.UserRepository;
import com.ensar.jobs.service.JobSeekerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobSeekerServiceImpl implements JobSeekerService {
    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public JobSeekerDTO createJobSeeker(JobSeekerDTO dto) {
        JobSeeker entity = modelMapper.map(dto, JobSeeker.class);
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId()).orElseThrow();
            entity.setUser(user);
        }
        return modelMapper.map(jobSeekerRepository.save(entity), JobSeekerDTO.class);
    }

    @Override
    public JobSeekerDTO updateJobSeeker(String id, JobSeekerDTO dto) {
        JobSeeker entity = jobSeekerRepository.findById(id).orElseThrow();
        modelMapper.map(dto, entity);
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId()).orElseThrow();
            entity.setUser(user);
        }
        return modelMapper.map(jobSeekerRepository.save(entity), JobSeekerDTO.class);
    }

    @Override
    public JobSeekerDTO getJobSeekerById(String id) {
        return jobSeekerRepository.findById(id)
            .map(entity -> modelMapper.map(entity, JobSeekerDTO.class))
            .orElse(null);
    }

    @Override
    public List<JobSeekerDTO> getAllJobSeekers() {
        return jobSeekerRepository.findAll().stream()
            .map(entity -> modelMapper.map(entity, JobSeekerDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteJobSeeker(String id) {
        jobSeekerRepository.deleteById(id);
    }
} 
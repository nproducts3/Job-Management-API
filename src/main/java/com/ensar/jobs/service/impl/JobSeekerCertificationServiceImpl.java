package com.ensar.jobs.service.impl;

import com.ensar.jobs.dto.JobSeekerCertificationDTO;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.entity.JobSeekerCertification;
import com.ensar.jobs.repository.JobSeekerCertificationRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import com.ensar.jobs.service.JobSeekerCertificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobSeekerCertificationServiceImpl implements JobSeekerCertificationService {
    @Autowired
    private JobSeekerCertificationRepository certificationRepository;
    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public JobSeekerCertificationDTO createJobSeekerCertification(JobSeekerCertificationDTO dto) {
        JobSeekerCertification entity = modelMapper.map(dto, JobSeekerCertification.class);
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId()).orElseThrow();
            entity.setJobSeeker(jobSeeker);
        }
        return modelMapper.map(certificationRepository.save(entity), JobSeekerCertificationDTO.class);
    }

    @Override
    public JobSeekerCertificationDTO updateJobSeekerCertification(String id, JobSeekerCertificationDTO dto) {
        JobSeekerCertification entity = certificationRepository.findById(id).orElseThrow();
        modelMapper.map(dto, entity);
        if (dto.getJobSeekerId() != null) {
            JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId()).orElseThrow();
            entity.setJobSeeker(jobSeeker);
        }
        return modelMapper.map(certificationRepository.save(entity), JobSeekerCertificationDTO.class);
    }

    @Override
    public JobSeekerCertificationDTO getJobSeekerCertificationById(String id) {
        return certificationRepository.findById(id)
            .map(entity -> modelMapper.map(entity, JobSeekerCertificationDTO.class))
            .orElse(null);
    }

    @Override
    public List<JobSeekerCertificationDTO> getAllJobSeekerCertifications() {
        return certificationRepository.findAll().stream()
            .map(entity -> modelMapper.map(entity, JobSeekerCertificationDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteJobSeekerCertification(String id) {
        certificationRepository.deleteById(id);
    }
} 
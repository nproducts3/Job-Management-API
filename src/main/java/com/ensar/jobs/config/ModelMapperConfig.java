package com.ensar.jobs.config;

import com.ensar.jobs.dto.*;
import com.ensar.jobs.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setSkipNullEnabled(true)
            .setAmbiguityIgnored(true);
        
        // JobSeeker mapping
        modelMapper.createTypeMap(JobSeeker.class, JobSeekerDTO.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getUser() != null ? src.getUser().getId() : null, 
                    JobSeekerDTO::setUserId);
            });

        // JobSeekerSkill mapping
        modelMapper.createTypeMap(JobSeekerSkill.class, JobSeekerSkillDTO.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getJobSeeker() != null ? src.getJobSeeker().getId() : null, 
                    JobSeekerSkillDTO::setJobSeekerId);
            });

        // JobSeekerExperience mapping
        modelMapper.createTypeMap(JobSeekerExperience.class, JobSeekerExperienceDTO.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getJobSeeker() != null ? src.getJobSeeker().getId() : null, 
                    JobSeekerExperienceDTO::setJobSeekerId);
                mapper.map(src -> src.getStartDate() != null ? src.getStartDate().toString() : null, 
                    JobSeekerExperienceDTO::setStartDate);
                mapper.map(src -> src.getEndDate() != null ? src.getEndDate().toString() : null, 
                    JobSeekerExperienceDTO::setEndDate);
            });

        // JobSeekerEducation mapping
        modelMapper.createTypeMap(JobSeekerEducation.class, JobSeekerEducationDTO.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getJobSeeker() != null ? src.getJobSeeker().getId() : null, 
                    JobSeekerEducationDTO::setJobSeekerId);
            });

        // JobSeekerCertification mapping
        modelMapper.createTypeMap(JobSeekerCertification.class, JobSeekerCertificationDTO.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getJobSeeker() != null ? src.getJobSeeker().getId() : null, 
                    JobSeekerCertificationDTO::setJobSeekerId);
            });

        return modelMapper;
    }
} 
package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobSeekerSkillDTO;
import java.util.List;

public interface JobSeekerSkillService {
    JobSeekerSkillDTO createJobSeekerSkill(JobSeekerSkillDTO dto);
    JobSeekerSkillDTO updateJobSeekerSkill(String id, JobSeekerSkillDTO dto);
    JobSeekerSkillDTO getJobSeekerSkillById(String id);
    List<JobSeekerSkillDTO> getAllJobSeekerSkills();
    void deleteJobSeekerSkill(String id);
} 
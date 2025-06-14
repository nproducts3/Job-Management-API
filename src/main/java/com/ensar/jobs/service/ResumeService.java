package com.ensar.jobs.service;

import com.ensar.jobs.dto.ResumeDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ResumeService {
    ResumeDTO uploadResume(MultipartFile file, String fullName, String email, String phoneNumber);
    ResumeDTO getResumeById(Integer id);
    Map<String, Object> matchResumeWithJob(Integer resumeId, Integer jobId);
} 
package com.ensar.jobs.service;

import com.ensar.jobs.dto.GoogleJobDTO;
import java.util.List;

public interface GoogleJobService {
    GoogleJobDTO createGoogleJob(GoogleJobDTO googleJobDTO);
    GoogleJobDTO updateGoogleJob(String id, GoogleJobDTO googleJobDTO);
    GoogleJobDTO getGoogleJobById(String id);
    void deleteGoogleJob(String id);
    List<GoogleJobDTO> getAllGoogleJobs();
    // Add more methods as needed
} 
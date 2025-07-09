package com.ensar.jobs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;
import com.ensar.jobs.entity.JobTitle;
import com.ensar.jobs.entity.City;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleJobDTO {
    private String id;
    private String jobId;
    private String title;
    private String companyName;
    private String location;
    private String via;
    private String shareLink;
    private String postedAt;
    private String salary;
    private String scheduleType;
    private String qualifications;
    private String description;
    private List<String> responsibilities;
    private List<String> benefits;
    private String applyLinks;
    private String createdDateTime;
    private String lastUpdatedDateTime;
    private JobTitle jobTitle;
    private City city;
} 
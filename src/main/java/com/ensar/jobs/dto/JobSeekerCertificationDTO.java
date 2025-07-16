package com.ensar.jobs.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class JobSeekerCertificationDTO {
    private String id;
    private String jobSeekerId;
    private String certificationName;
    private String certificationFile;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private String issuingOrganization;
} 
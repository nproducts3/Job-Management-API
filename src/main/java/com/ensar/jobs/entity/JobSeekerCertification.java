package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDate;

@Entity
@Table( name = "job_seeker_certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobSeekerCertification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    @Column(name = "certification_name")
    private String certificationName;
    
    @Column(name = "certification_file")
    private String certificationFile;
    
    @Column(name = "issued_date")
    private LocalDate issuedDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "issuing_organization")
    private String issuingOrganization;
} 
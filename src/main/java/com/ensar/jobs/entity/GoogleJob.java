package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "google_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GoogleJob extends BaseEntity {
    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(length = 36)
    private String id;

    @Column(name = "job_id", unique = true, nullable = false)
    private UUID jobId;

    @Column(nullable = false)
    private String title;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    private String location;
    private String via;

    @Column(name = "share_link", columnDefinition = "TEXT")
    private String shareLink;

    @Column(name = "posted_at")
    private String postedAt;

    private String salary;

    @Column(name = "schedule_type")
    private String scheduleType;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Convert(converter = com.ensar.jobs.entity.StringListJsonConverter.class)
    @Column(columnDefinition = "json")
    private List<String> responsibilities;

    @Convert(converter = com.ensar.jobs.entity.StringListJsonConverter.class)
    @Column(columnDefinition = "json")
    private List<String> benefits;

    @Column(name = "apply_links", columnDefinition = "TEXT")
    private String applyLinks;

  

    @Column(name = "job_title_id", length = 36)
    private String jobTitleId;

    @Column(name = "city_id", length = 36)
    private String cityId;
} 
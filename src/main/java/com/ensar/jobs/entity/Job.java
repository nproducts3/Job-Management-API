package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ensar.jobs.converter.WorkTypeConverter;
import com.ensar.jobs.converter.WorkEnvironmentConverter;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    
    @Id
    private String id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company companyEntity;

    @Column(length = 100)
    private String department;

    @Column(name = "job_title_id")
    private Integer jobTitleId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String salary;

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column(name = "salary_currency", length = 10)
    private String salaryCurrency = "INR";

    @Column(length = 50)
    private String experience;

    @Column(name = "experience_min")
    private Integer experienceMin;

    @Column(name = "experience_max")
    private Integer experienceMax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(length = 200)
    private String location;

    @Column(name = "work_type")
    @Convert(converter = WorkTypeConverter.class)
    private WorkType workType;

    @Column(name = "work_environment")
    @Convert(converter = WorkEnvironmentConverter.class)
    private WorkEnvironment workEnvironment;

    @Column(columnDefinition = "json")
    private String skills;

    @Column(columnDefinition = "json")
    private String requirements;

    @Column(columnDefinition = "json")
    private String benefits;

    @Column(columnDefinition = "json")
    private String responsibilities;

    @Column(name = "apply_url", columnDefinition = "TEXT")
    private String applyUrl;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.Active;

    @Column(columnDefinition = "json")
    private String categories;

    private BigDecimal rating;

    private String reviews;

    @Column(name = "applications_count")
    private Integer applicationsCount = 0;

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id")
    private User recruiter;

    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (postedDate == null) {
            postedDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum WorkType {
        FULL_TIME("Full-time"),
        PART_TIME("Part-time"),
        CONTRACT("Contract"),
        INTERNSHIP("Internship"),
        FREELANCE("Freelance");

        private final String value;

        WorkType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @JsonCreator
        public static WorkType fromValue(String value) {
            if (value == null) {
                return null;
            }
            for (WorkType type : values()) {
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown WorkType: " + value);
        }

        @JsonValue
        public String toValue() {
            return this.value;
        }
    }

    public enum WorkEnvironment {
        REMOTE("Remote"),
        WORK_FROM_OFFICE("Work from office"),
        HYBRID("Hybrid"),
        TEMP_WFH("Temp. WFH due to...");

        private final String value;

        WorkEnvironment(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @JsonCreator
        public static WorkEnvironment fromValue(String value) {
            if (value == null) {
                return null;
            }
            for (WorkEnvironment env : values()) {
                if (env.getValue().equalsIgnoreCase(value)) {
                    return env;
                }
            }
            throw new IllegalArgumentException("Unknown WorkEnvironment: " + value);
        }

        @JsonValue
        public String toValue() {
            return this.value;
        }
    }

    public enum JobStatus {
        Active("Active"),
        Inactive("Inactive"),
        Expired("Expired"),
        Filled("Filled");

        private final String value;

        JobStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
} 
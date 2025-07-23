-- V1__init.sql

-- 1. Role Table
CREATE TABLE `role` (
  `id` char(36) NOT NULL,
  `role_name` varchar(255) NOT NULL,
  `role_description` varchar(255) DEFAULT NULL,
  `role_permission` varchar(255) DEFAULT NULL,
  `created_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated_date_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 2. Organization Table
CREATE TABLE `organization` (
  `id` char(36) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(500) NOT NULL,
  `domain` varchar(50) NOT NULL,
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  `logo_img_src` text,
  `created_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated_date_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `org_name_unique` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 3. Users Table
CREATE TABLE `users` (
  `id` char(36) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  `created_date_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated_date_time` timestamp NULL DEFAULT NULL,
  `role_id` char(36) NOT NULL,
  `organization_id` char(36) NOT NULL,
  `email_verified` bit(1) DEFAULT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `last_name` varchar(100) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `user_email_unique` (`email`),
  KEY `idx_email` (`email`),
  KEY `FK9q8fdenwsqjwrjfivd5ovv5k3` (`organization_id`),
  KEY `FK4qu1gr772nnf6ve5af002rwya` (`role_id`),
  CONSTRAINT `FK4qu1gr772nnf6ve5af002rwya` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FK9q8fdenwsqjwrjfivd5ovv5k3` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 5. Job Titles Table
CREATE TABLE `job_titles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `created_date_time` datetime(6) NOT NULL,
  `last_updated_date_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `title` (`title`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. Cities Table
CREATE TABLE `cities` (
  `id` int NOT NULL AUTO_INCREMENT,
  `rankn` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `state` varchar(100) DEFAULT NULL,
  `population` bigint DEFAULT NULL,
  `growth` double DEFAULT NULL,
  `created_date_time` datetime(6) NOT NULL,
  `last_updated_date_time` datetime(6) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `rankin` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7. Google Jobs Table
CREATE TABLE `google_jobs` (
  `id` varchar(255) NOT NULL,
  `job_id` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `company_name` varchar(255) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `via` varchar(255) DEFAULT NULL,
  `share_link` text,
  `posted_at` varchar(255) DEFAULT NULL,
  `salary` varchar(255) DEFAULT NULL,
  `schedule_type` varchar(255) DEFAULT NULL,
  `qualifications` text,
  `description` text,
  `responsibilities` json DEFAULT NULL,
  `benefits` json DEFAULT NULL,
  `apply_links` text,
  `created_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated_date_time` timestamp NULL DEFAULT NULL,
  `job_title_id` int DEFAULT NULL,
  `city_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_id` (`job_id`),
  KEY `fk_job_title1` (`job_title_id`),
  KEY `fk_city1` (`city_id`),
  CONSTRAINT `fk_city1` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`),
  CONSTRAINT `fk_job_title1` FOREIGN KEY (`job_title_id`) REFERENCES `job_titles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;




-- 10. Job Seekers Table
CREATE TABLE job_seekers (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    location VARCHAR(255),
    phone VARCHAR(50),
    desired_salary VARCHAR(50),
    preferred_job_types VARCHAR(255),
    created_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_date_time TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_seeker_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 9. Job Resumes Table
CREATE TABLE `job_resumes` (
    `id` CHAR(36) PRIMARY KEY,
    `googlejob_id` VARCHAR(255) NOT NULL,
    `job_seeker_id` CHAR(36) NULL,
    `resume_file` VARCHAR(255) NOT NULL,
    `resume_text` TEXT,
    `match_percentage` DECIMAL(5,2) DEFAULT 0.00,
    `uploaded_at` DATETIME,
    created_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_date_time TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_job_resume_job` FOREIGN KEY (`googlejob_id`) REFERENCES `google_jobs` (`id`),
    CONSTRAINT `fk_job_resume_jobseeker` FOREIGN KEY (`job_seeker_id`) REFERENCES `job_seekers`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 11. Job Seeker Skills Table
CREATE TABLE job_seeker_skills (
    id CHAR(36) PRIMARY KEY,
    job_seeker_id CHAR(36) NOT NULL,
    skill_name VARCHAR(255) NOT NULL,
    created_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_date_time TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_seeker_skill FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 12. Job Seeker Experience Table
CREATE TABLE job_seeker_experience (
    id CHAR(36) PRIMARY KEY,
    job_seeker_id CHAR(36) NOT NULL,
    job_title VARCHAR(255),
    company_name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    responsibilities JSON,
    created_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_date_time TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_seeker_experience FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 13. Job Seeker Education Table
CREATE TABLE job_seeker_education (
    id CHAR(36) PRIMARY KEY,
    job_seeker_id CHAR(36) NOT NULL,
    degree VARCHAR(255),
    university VARCHAR(255),
    graduation_year INT,
    created_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_date_time TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_seeker_education FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 14. Job Seeker Certifications Table
CREATE TABLE job_seeker_certifications (
    id CHAR(36) PRIMARY KEY,
    job_seeker_id CHAR(36) NOT NULL,
    certification_name VARCHAR(255),
    created_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_date_time TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_seeker_certification FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


   ALTER TABLE google_jobs MODIFY responsibilities JSON;
   ALTER TABLE google_jobs MODIFY benefits JSON;
ALTER TABLE job_resumes MODIFY resume_text LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Add ai_suggestions column for new DBs
ALTER TABLE job_resumes ADD COLUMN ai_suggestions TEXT;
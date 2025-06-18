-- V1__init.sql

-- 1. Role Table
CREATE TABLE role (
    id CHAR(36) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_description VARCHAR(255),
    role_permission VARCHAR(255),
    created_date_time DATETIME,
    last_updated_date_time DATETIME
);

-- 2. Organization Table
CREATE TABLE organization (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    domain VARCHAR(100) NOT NULL,
    disabled TINYINT(1) DEFAULT 0,
    logo_img_src TEXT,
    created_date_time DATETIME,
    last_updated_date_time DATETIME
);

-- 3. Users Table
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_date_time DATETIME,
    last_updated_date_time DATETIME,
    role_id CHAR(36),
    organization_id CHAR(36),
    email_verified BOOLEAN DEFAULT FALSE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    last_login DATETIME,
    phone_number VARCHAR(20),
    profile_picture VARCHAR(255),
    disabled BOOLEAN DEFAULT FALSE,
    updated_at DATETIME,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT fk_user_org FOREIGN KEY (organization_id) REFERENCES organization(id)
);

-- 4. Companies Table
CREATE TABLE companies (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    logo VARCHAR(10),
    logo_url TEXT,
    website TEXT,
    industry VARCHAR(100),
    size VARCHAR(50),
    employee_count INT,
    founded INT,
    headquarters VARCHAR(100),
    rating DECIMAL(2,1),
    reviews VARCHAR(100),
    reviews_count INT,
    job_count INT,
    color VARCHAR(20),
    is_verified TINYINT(1) DEFAULT 0,
    is_featured TINYINT(1) DEFAULT 0,
    benefits JSON,
    culture TEXT,
    linkedin VARCHAR(255),
    twitter VARCHAR(255),
    facebook VARCHAR(255),
    instagram VARCHAR(255),
    locations JSON,
    organization_id CHAR(36),
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_company_org FOREIGN KEY (organization_id) REFERENCES organization(id)
);

-- 5. Job Titles Table
CREATE TABLE job_titles (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    created_date_time DATETIME,
    last_updated_date_time DATETIME
);

-- 6. Cities Table
CREATE TABLE cities (
    id CHAR(36) PRIMARY KEY,
    rankin INT,
    name VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100),
    population INT,
    growth DOUBLE,
    created_date_time DATETIME,
    last_updated_date_time DATETIME
);

-- 7. Google Jobs Table
CREATE TABLE google_jobs (
  id CHAR(36) NOT NULL,
  job_id CHAR(36) NOT NULL,
  title VARCHAR(255) NOT NULL,
  company_name VARCHAR(255) NOT NULL,
  location VARCHAR(255) DEFAULT NULL,
  via VARCHAR(255) DEFAULT NULL,
  share_link TEXT,
  posted_at VARCHAR(255) DEFAULT NULL,
  salary VARCHAR(255) DEFAULT NULL,
  schedule_type VARCHAR(255) DEFAULT NULL,
  qualifications TEXT,
  description TEXT,
  responsibilities JSON DEFAULT NULL,
  benefits JSON DEFAULT NULL,
  apply_links TEXT,
  created_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_date_time TIMESTAMP NULL DEFAULT NULL,
  job_title_id CHAR(36) DEFAULT NULL,
  city_id CHAR(36) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY job_id (job_id),
  KEY fk_job_title1 (job_title_id),
  KEY fk_city1 (city_id),
  CONSTRAINT fk_city1 FOREIGN KEY (city_id) REFERENCES cities (id),
  CONSTRAINT fk_job_title1 FOREIGN KEY (job_title_id) REFERENCES job_titles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 8. Resumes Table


-- 9. Job Resumes Table
CREATE TABLE job_resumes (
    id CHAR(36) PRIMARY KEY,
    googlejob_id CHAR(36) NOT NULL,
    resume_file VARCHAR(255) NOT NULL,
    resume_text TEXT,
    match_percentage DECIMAL(5,2) DEFAULT 0.00,
    uploaded_at DATETIME,
    CONSTRAINT fk_job_resume_job FOREIGN KEY (googlejob_id) REFERENCES google_jobs(id)
);

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
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_job_seeker_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 11. Job Seeker Skills Table
CREATE TABLE job_seeker_skills (
    id CHAR(36) PRIMARY KEY,
    job_seeker_id CHAR(36) NOT NULL,
    skill_name VARCHAR(255) NOT NULL,
    CONSTRAINT fk_job_seeker_skill FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id)
);

-- 12. Job Seeker Experience Table
CREATE TABLE job_seeker_experience (
    id CHAR(36) PRIMARY KEY,
    job_seeker_id CHAR(36) NOT NULL,
    job_title VARCHAR(255),
    company_name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    responsibilities JSON,
    CONSTRAINT fk_job_seeker_experience FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id)
);

-- 13. Job Seeker Education Table
CREATE TABLE job_seeker_education (
    id CHAR(36) PRIMARY KEY,
    job_seeker_id CHAR(36) NOT NULL,
    degree VARCHAR(255),
    university VARCHAR(255),
    graduation_year INT,
    CONSTRAINT fk_job_seeker_education FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id)
);

-- 14. Job Seeker Certifications Table
CREATE TABLE job_seeker_certifications (
    id CHAR(36) PRIMARY KEY,
    job_seeker_id CHAR(36) NOT NULL,
    certification_name VARCHAR(255),
    CONSTRAINT fk_job_seeker_certification FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id)
);

-- Sample Data
INSERT INTO role (id, role_name, role_description, role_permission, created_date_time, last_updated_date_time) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789a01', 'ROLE_ADMIN', 'Administrator role', 'ALL', NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789a02', 'ROLE_USER', 'User role', 'READ', NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789a03', 'ROLE_RECRUITER', 'Recruiter role', 'READ,WRITE', NOW(), NOW());

INSERT INTO organization (id, name, description, domain, disabled, created_date_time, last_updated_date_time) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789b01', 'Default Organization', 'Default org for users', 'default.com', 0, NOW(), NOW());

INSERT INTO users (id, username, email, password, created_date_time, last_updated_date_time, role_id, organization_id, email_verified, first_name, last_name, disabled) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789c01', 'admin', 'admin@example.com', 'adminpass', NOW(), NOW(), 'b3e1c1e2-1234-4a5b-8c6d-123456789a01', 'b3e1c1e2-1234-4a5b-8c6d-123456789b01', 1, 'Admin', 'User', 0),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789c02', 'user', 'user@example.com', 'userpass', NOW(), NOW(), 'b3e1c1e2-1234-4a5b-8c6d-123456789a02', 'b3e1c1e2-1234-4a5b-8c6d-123456789b01', 1, 'Regular', 'User', 0);

INSERT INTO companies (id, name, description, created_at, updated_at) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789d01', 'Acme Corp', 'A leading company', NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789d02', 'Globex Inc', 'Global experts', NOW(), NOW());

INSERT INTO job_titles (id, title, created_date_time, last_updated_date_time) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789e01', 'Software Engineer', NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789e02', 'Data Analyst', NOW(), NOW());

INSERT INTO cities (id, name, state, country, population, growth, created_date_time, last_updated_date_time) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789f01', 'New York', 'NY', 'USA', 8000000, 1.2, NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789f02', 'San Francisco', 'CA', 'USA', 870000, 1.1, NOW(), NOW());

-- Google Jobs
INSERT INTO google_jobs (id, job_id, title, company_name, location, via, share_link, posted_at, salary, schedule_type, qualifications, description, responsibilities, benefits, apply_links, created_date_time, last_updated_date_time, job_title_id, city_id)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456790001', 'b3e1c1e2-1234-4a5b-8c6d-123456790001', 'Software Engineer', 'Acme Corp', 'New York', 'Indeed', 'https://share.example.com/gjob-1', '2024-06-01', '100000', 'Full-time', 'BSc in CS', 'Develop and maintain software.', '["Develop features"]', '["Health Insurance"]', 'https://apply.example.com/gjob-1', NOW(), NOW(), 'b3e1c1e2-1234-4a5b-8c6d-123456789e01', 'b3e1c1e2-1234-4a5b-8c6d-123456789f01'),
  ('b3e1c1e2-1234-4a5b-8c6d-123456790002', 'b3e1c1e2-1234-4a5b-8c6d-123456790002', 'Data Analyst', 'Globex Inc', 'San Francisco', 'LinkedIn', 'https://share.example.com/gjob-2', '2024-06-02', '80000', 'Full-time', 'BSc in Stats', 'Analyze data and generate reports.', '["Analyze data"]', '["Gym Membership"]', 'https://apply.example.com/gjob-2', NOW(), NOW(), 'b3e1c1e2-1234-4a5b-8c6d-123456789e02', 'b3e1c1e2-1234-4a5b-8c6d-123456789f02');

-- Job Seekers
INSERT INTO job_seekers (id, user_id, first_name, last_name, location, phone, desired_salary, preferred_job_types, created_at, updated_at)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-12345678c001', 'b3e1c1e2-1234-4a5b-8c6d-123456789c02', 'Alice', 'Smith', 'New York', '1234567890', '110000', 'FULL_TIME', NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-12345678c002', 'b3e1c1e2-1234-4a5b-8c6d-123456789c02', 'Bob', 'Johnson', 'San Francisco', '0987654321', '85000', 'FULL_TIME', NOW(), NOW());

-- Job Seeker Skills
INSERT INTO job_seeker_skills (id, job_seeker_id, skill_name)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-12345678d001', 'b3e1c1e2-1234-4a5b-8c6d-12345678c001', 'Java'),
  ('b3e1c1e2-1234-4a5b-8c6d-12345678d002', 'b3e1c1e2-1234-4a5b-8c6d-12345678c001', 'Spring Boot'),
  ('b3e1c1e2-1234-4a5b-8c6d-12345678d003', 'b3e1c1e2-1234-4a5b-8c6d-12345678c002', 'SQL'),
  ('b3e1c1e2-1234-4a5b-8c6d-12345678d004', 'b3e1c1e2-1234-4a5b-8c6d-12345678c002', 'Python');

-- Job Seeker Experience
INSERT INTO job_seeker_experience (id, job_seeker_id, job_title, company_name, start_date, end_date, responsibilities)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-12345678e001', 'b3e1c1e2-1234-4a5b-8c6d-12345678c001', 'Software Engineer', 'Acme Corp', '2021-01-01', '2023-01-01', '["Developed features"]'),
  ('b3e1c1e2-1234-4a5b-8c6d-12345678e002', 'b3e1c1e2-1234-4a5b-8c6d-12345678c002', 'Data Analyst', 'Globex Inc', '2020-01-01', '2023-01-01', '["Analyzed data"]');

-- Job Seeker Education
INSERT INTO job_seeker_education (id, job_seeker_id, degree, university, graduation_year)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-12345678f001', 'b3e1c1e2-1234-4a5b-8c6d-12345678c001', 'BSc Computer Science', 'NYU', 2020),
  ('b3e1c1e2-1234-4a5b-8c6d-12345678f002', 'b3e1c1e2-1234-4a5b-8c6d-12345678c002', 'BSc Statistics', 'Stanford', 2019);

-- Job Seeker Certifications
INSERT INTO job_seeker_certifications (id, job_seeker_id, certification_name)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456780001', 'b3e1c1e2-1234-4a5b-8c6d-12345678c001', 'Oracle Certified Java Programmer'),
  ('b3e1c1e2-1234-4a5b-8c6d-123456780002', 'b3e1c1e2-1234-4a5b-8c6d-12345678c002', 'Google Data Analytics');

-- Job Resumes
INSERT INTO job_resumes (id, googlejob_id, resume_file, resume_text, match_percentage, uploaded_at)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456781001', 'b3e1c1e2-1234-4a5b-8c6d-123456790001', '/resumes/alice.pdf', 'Experienced software engineer...', 95.00, NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456781002', 'b3e1c1e2-1234-4a5b-8c6d-123456790002', '/resumes/bob.pdf', 'Data analyst with 3 years experience...', 90.00, NOW()); 
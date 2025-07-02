-- Sample Data
INSERT INTO role (id, role_name, role_description, role_permission, created_date_time, last_updated_date_time) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789a01', 'ROLE_ADMIN', 'Administrator role', 'ALL', NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789a02', 'ROLE_JOBSEEKER', 'User role', 'READ', NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789a03', 'ROLE_EMPLOYER', 'Recruiter role', 'READ,WRITE', NOW(), NOW());

INSERT INTO organization (id, name, description, domain, disabled, created_date_time, last_updated_date_time) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789b01', 'Default Organization', 'Default org for users', 'default.com', 0, NOW(), NOW());

INSERT INTO users (id, username, email, password, created_date_time, last_updated_date_time, role_id, organization_id, email_verified, first_name, last_name, disabled) VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456789c01', 'admin', 'admin@example.com', '$2a$12$DDTGUSeYV34XlDPgY2VXS.D0pqPzPeu/X.iaaiEif20X9iHhU97o2', NOW(), NOW(), 'b3e1c1e2-1234-4a5b-8c6d-123456789a01', 'b3e1c1e2-1234-4a5b-8c6d-123456789b01', 1, 'Admin', 'User', 0),
  ('b3e1c1e2-1234-4a5b-8c6d-123456789c02', 'user', 'user@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', NOW(), NOW(), 'b3e1c1e2-1234-4a5b-8c6d-123456789a02', 'b3e1c1e2-1234-4a5b-8c6d-123456789b01', 1, 'Regular', 'User', 0);

-- job_titles: explicit IDs
INSERT INTO job_titles (id, title, created_date_time, last_updated_date_time) VALUES
  (1, 'Software Engineer', NOW(), NOW()),
  (2, 'Data Analyst', NOW(), NOW());

-- cities: explicit IDs and column order as per schema
INSERT INTO cities (id, rankn, name, state, population, growth, created_date_time, last_updated_date_time, country, rankin) VALUES
  (1, 1, 'New York', 'NY', 8000000, 1.2, NOW(), NOW(), 'USA', 1),
  (2, 2, 'San Francisco', 'CA', 870000, 1.1, NOW(), NOW(), 'USA', 2);

-- Google Jobs: use job_title_id and city_id as 1 and 1, 2 and 2 (assuming explicit IDs)
INSERT INTO google_jobs (id, job_id, title, company_name, location, via, share_link, posted_at, salary, schedule_type, qualifications, description, responsibilities, benefits, apply_links, created_date_time, last_updated_date_time, job_title_id, city_id)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456790001', 'b3e1c1e2-1234-4a5b-8c6d-123456790001', 'Software Engineer', 'Acme Corp', 'New York', 'Indeed', 'https://share.example.com/gjob-1', '2024-06-01', '100000', 'Full-time', 'BSc in CS', 'Develop and maintain software.', '["Develop features"]', '["Health Insurance"]', 'https://apply.example.com/gjob-1', NOW(), NOW(), 1, 1),
  ('b3e1c1e2-1234-4a5b-8c6d-123456790002', 'b3e1c1e2-1234-4a5b-8c6d-123456790002', 'Data Analyst', 'Globex Inc', 'San Francisco', 'LinkedIn', 'https://share.example.com/gjob-2', '2024-06-02', '80000', 'Full-time', 'BSc in Stats', 'Analyze data and generate reports.', '["Analyze data"]', '["Gym Membership"]', 'https://apply.example.com/gjob-2', NOW(), NOW(), 2, 2);

-- Job Seekers
INSERT INTO job_seekers (id, user_id, first_name, last_name, location, phone, desired_salary, preferred_job_types, created_date_time, last_updated_date_time)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-12345678c001', 'b3e1c1e2-1234-4a5b-8c6d-123456789c01', 'Alice', 'Smith', 'New York', '1234567890', '110000', 'FULL_TIME', NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-12345678c002', 'b3e1c1e2-1234-4a5b-8c6d-123456789c01', 'Bob', 'Johnson', 'San Francisco', '0987654321', '85000', 'FULL_TIME', NOW(), NOW());

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
INSERT INTO job_resumes (id, googlejob_id, resume_file, resume_text, match_percentage, uploaded_at, created_date_time, last_updated_date_time)
VALUES
  ('b3e1c1e2-1234-4a5b-8c6d-123456781001', 'b3e1c1e2-1234-4a5b-8c6d-123456790001', '/resumes/alice.pdf', 'Experienced software engineer...', 95.00, NOW(), NOW(), NOW()),
  ('b3e1c1e2-1234-4a5b-8c6d-123456781002', 'b3e1c1e2-1234-4a5b-8c6d-123456790002', '/resumes/bob.pdf', 'Data analyst with 3 years experience...', 90.00, NOW(), NOW(), NOW());

ALTER TABLE google_jobs ADD INDEX idx_created_date_time (created_date_time);



ALTER TABLE users MODIFY organization_id CHAR(36) NULL;
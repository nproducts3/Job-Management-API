ALTER TABLE job_resumes
ADD COLUMN job_seeker_id CHAR(36) NOT NULL,
ADD CONSTRAINT fk_job_resume_jobseeker FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id); 
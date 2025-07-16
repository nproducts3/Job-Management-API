-- Add certification file fields to job_seeker_certifications table
ALTER TABLE job_seeker_certifications 
ADD COLUMN certification_file VARCHAR(255) NULL,
ADD COLUMN issued_date DATE NULL,
ADD COLUMN expiry_date DATE NULL,
ADD COLUMN issuing_organization VARCHAR(255) NULL; 
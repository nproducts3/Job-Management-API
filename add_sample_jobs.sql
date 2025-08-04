-- Add more sample Google Jobs for testing pagination
-- This script adds 15 more jobs to test pagination with 10 jobs per page

INSERT INTO google_jobs (id, job_id, title, company_name, location, via, share_link, posted_at, salary, schedule_type, qualifications, description, responsibilities, benefits, apply_links, created_date_time, last_updated_date_time, job_title_id, city_id)
VALUES
-- Data Science & Analytics Jobs
('b3e1c1e2-1234-4a5b-8c6d-123456790003', 'b3e1c1e2-1234-4a5b-8c6d-123456790003', 'Senior Data Scientist', 'TechCorp Analytics', 'San Francisco, CA', 'LinkedIn', 'https://share.example.com/gjob-3', '2024-06-03', '150000', 'Full-time', 'MSc in Data Science, 5+ years experience', 'Lead data science initiatives and develop machine learning models.', '["Develop ML models", "Data analysis", "Team leadership"]', '["Health insurance", "401k", "Stock options"]', 'https://apply.example.com/gjob-3', NOW(), NOW(), 1, 1),

('b3e1c1e2-1234-4a5b-8c6d-123456790004', 'b3e1c1e2-1234-4a5b-8c6d-123456790004', 'Business Intelligence Analyst', 'DataFlow Inc', 'Austin, TX', 'Indeed', 'https://share.example.com/gjob-4', '2024-06-04', '85000', 'Full-time', 'BSc in Business Analytics, 3+ years experience', 'Create BI dashboards and reports for business stakeholders.', '["Create dashboards", "Data visualization", "Report generation"]', '["Health insurance", "Remote work"]', 'https://apply.example.com/gjob-4', NOW(), NOW(), 2, 2),

('b3e1c1e2-1234-4a5b-8c6d-123456790005', 'b3e1c1e2-1234-4a5b-8c6d-123456790005', 'Python Developer', 'CodeCraft Solutions', 'Seattle, WA', 'Glassdoor', 'https://share.example.com/gjob-5', '2024-06-05', '120000', 'Full-time', 'BSc in Computer Science, Python expertise', 'Develop Python applications and APIs.', '["Python development", "API design", "Code review"]', '["Health insurance", "401k", "Flexible hours"]', 'https://apply.example.com/gjob-5', NOW(), NOW(), 1, 1),

-- Frontend Development Jobs
('b3e1c1e2-1234-4a5b-8c6d-123456790006', 'b3e1c1e2-1234-4a5b-8c6d-123456790006', 'React Developer', 'WebTech Innovations', 'Boston, MA', 'LinkedIn', 'https://share.example.com/gjob-6', '2024-06-06', '110000', 'Full-time', 'BSc in Computer Science, React experience', 'Build modern web applications using React.', '["React development", "UI/UX", "Frontend optimization"]', '["Health insurance", "Remote work", "Learning budget"]', 'https://apply.example.com/gjob-6', NOW(), NOW(), 1, 2),

('b3e1c1e2-1234-4a5b-8c6d-123456790007', 'b3e1c1e2-1234-4a5b-8c6d-123456790007', 'Angular Developer', 'Frontend Masters', 'Chicago, IL', 'Indeed', 'https://share.example.com/gjob-7', '2024-06-07', '105000', 'Full-time', 'BSc in Computer Science, Angular expertise', 'Develop enterprise applications using Angular.', '["Angular development", "TypeScript", "Component design"]', '["Health insurance", "401k", "Professional development"]', 'https://apply.example.com/gjob-7', NOW(), NOW(), 1, 1),

-- Backend Development Jobs
('b3e1c1e2-1234-4a5b-8c6d-123456790008', 'b3e1c1e2-1234-4a5b-8c6d-123456790008', 'Java Backend Developer', 'Enterprise Solutions', 'Denver, CO', 'Glassdoor', 'https://share.example.com/gjob-8', '2024-06-08', '125000', 'Full-time', 'BSc in Computer Science, Java expertise', 'Build scalable backend services using Java.', '["Java development", "Spring Boot", "Microservices"]', '["Health insurance", "401k", "Stock options"]', 'https://apply.example.com/gjob-8', NOW(), NOW(), 1, 2),

('b3e1c1e2-1234-4a5b-8c6d-123456790009', 'b3e1c1e2-1234-4a5b-8c6d-123456790009', 'Node.js Developer', 'CloudTech Systems', 'Portland, OR', 'LinkedIn', 'https://share.example.com/gjob-9', '2024-06-09', '115000', 'Full-time', 'BSc in Computer Science, Node.js experience', 'Develop server-side applications using Node.js.', '["Node.js development", "Express.js", "API development"]', '["Health insurance", "Remote work", "Flexible hours"]', 'https://apply.example.com/gjob-9', NOW(), NOW(), 1, 1),

-- Database & DevOps Jobs
('b3e1c1e2-1234-4a5b-8c6d-123456790010', 'b3e1c1e2-1234-4a5b-8c6d-123456790010', 'Database Administrator', 'DataSystems Corp', 'Atlanta, GA', 'Indeed', 'https://share.example.com/gjob-10', '2024-06-10', '95000', 'Full-time', 'BSc in Computer Science, SQL expertise', 'Manage and optimize database systems.', '["Database management", "SQL optimization", "Performance tuning"]', '["Health insurance", "401k", "Professional certifications"]', 'https://apply.example.com/gjob-10', NOW(), NOW(), 2, 2),

('b3e1c1e2-1234-4a5b-8c6d-123456790011', 'b3e1c1e2-1234-4a5b-8c6d-123456790011', 'DevOps Engineer', 'CloudOps Solutions', 'Miami, FL', 'Glassdoor', 'https://share.example.com/gjob-11', '2024-06-11', '130000', 'Full-time', 'BSc in Computer Science, DevOps experience', 'Implement CI/CD pipelines and cloud infrastructure.', '["CI/CD", "Docker", "Kubernetes", "AWS"]', '["Health insurance", "401k", "Stock options"]', 'https://apply.example.com/gjob-11', NOW(), NOW(), 1, 1),

-- Testing & QA Jobs
('b3e1c1e2-1234-4a5b-8c6d-123456790012', 'b3e1c1e2-1234-4a5b-8c6d-123456790012', 'QA Automation Engineer', 'TestTech Inc', 'Phoenix, AZ', 'LinkedIn', 'https://share.example.com/gjob-12', '2024-06-12', '90000', 'Full-time', 'BSc in Computer Science, testing experience', 'Develop automated test suites and quality assurance processes.', '["Test automation", "Selenium", "Cypress", "Quality assurance"]', '["Health insurance", "401k", "Learning budget"]', 'https://apply.example.com/gjob-12', NOW(), NOW(), 2, 2),

-- Full Stack Jobs
('b3e1c1e2-1234-4a5b-8c6d-123456790013', 'b3e1c1e2-1234-4a5b-8c6d-123456790013', 'Full Stack Developer', 'WebSolutions Pro', 'Las Vegas, NV', 'Indeed', 'https://share.example.com/gjob-13', '2024-06-13', '140000', 'Full-time', 'BSc in Computer Science, full stack experience', 'Develop complete web applications from frontend to backend.', '["Full stack development", "React", "Node.js", "Database design"]', '["Health insurance", "401k", "Stock options", "Remote work"]', 'https://apply.example.com/gjob-13', NOW(), NOW(), 1, 1),

('b3e1c1e2-1234-4a5b-8c6d-123456790014', 'b3e1c1e2-1234-4a5b-8c6d-123456790014', 'MERN Stack Developer', 'ModernWeb Tech', 'Nashville, TN', 'Glassdoor', 'https://share.example.com/gjob-14', '2024-06-14', '135000', 'Full-time', 'BSc in Computer Science, MERN stack experience', 'Build applications using MongoDB, Express, React, and Node.js.', '["MERN stack", "MongoDB", "Express.js", "React", "Node.js"]', '["Health insurance", "401k", "Flexible hours"]', 'https://apply.example.com/gjob-14', NOW(), NOW(), 1, 2),

-- Machine Learning Jobs
('b3e1c1e2-1234-4a5b-8c6d-123456790015', 'b3e1c1e2-1234-4a5b-8c6d-123456790015', 'Machine Learning Engineer', 'AI Innovations', 'Raleigh, NC', 'LinkedIn', 'https://share.example.com/gjob-15', '2024-06-15', '160000', 'Full-time', 'MSc in Computer Science, ML experience', 'Design and implement machine learning models and systems.', '["Machine learning", "Python", "TensorFlow", "Deep learning"]', '["Health insurance", "401k", "Stock options", "Research budget"]', 'https://apply.example.com/gjob-15', NOW(), NOW(), 1, 1),

('b3e1c1e2-1234-4a5b-8c6d-123456790016', 'b3e1c1e2-1234-4a5b-8c6d-123456790016', 'Data Engineer', 'BigData Corp', 'Salt Lake City, UT', 'Indeed', 'https://share.example.com/gjob-16', '2024-06-16', '120000', 'Full-time', 'BSc in Computer Science, data engineering experience', 'Build and maintain data pipelines and infrastructure.', '["Data engineering", "ETL", "Apache Spark", "Data warehousing"]', '["Health insurance", "401k", "Professional development"]', 'https://apply.example.com/gjob-16', NOW(), NOW(), 2, 2),

('b3e1c1e2-1234-4a5b-8c6d-123456790017', 'b3e1c1e2-1234-4a5b-8c6d-123456790017', 'Product Manager', 'ProductVision Inc', 'Minneapolis, MN', 'Glassdoor', 'https://share.example.com/gjob-17', '2024-06-17', '110000', 'Full-time', 'BSc in Business, product management experience', 'Lead product development and strategy for software products.', '["Product management", "Agile", "User research", "Product strategy"]', '["Health insurance", "401k", "Stock options"]', 'https://apply.example.com/gjob-17', NOW(), NOW(), 2, 1);

-- Verify the insertion
SELECT COUNT(*) as total_jobs FROM google_jobs;
SELECT id, title, company_name, location FROM google_jobs ORDER BY created_date_time DESC LIMIT 10; 
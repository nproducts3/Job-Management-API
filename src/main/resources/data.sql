-- Cities
INSERT INTO cities (id, rankin, name, state, population, growth, country, created_date_time, last_updated_date_time) VALUES 
(1, 1, 'New York', 'New York', 8405837, 4.80, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'Los Angeles', 'California', 3884307, 4.80, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'Chicago', 'Illinois', 2718782, -6.10, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 'Houston', 'Texas', 2195914, 11.00, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 5, 'Philadelphia', 'Pennsylvania', 1553165, 2.60, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 6, 'Phoenix', 'Arizona', 1513367, 14.00, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 7, 'San Antonio', 'Texas', 1409019, 21.00, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 8, 'San Diego', 'California', 1355896, 10.50, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 9, 'Dallas', 'Texas', 1257676, 5.60, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 10, 'San Jose', 'California', 998537, 10.50, 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Roles
INSERT INTO roles (id, name, description) VALUES 
(1, 'ROLE_ADMIN', 'Administrator role with full access'),
(2, 'ROLE_HR', 'Human Resources role'),
(3, 'ROLE_RECRUITER', 'Recruiter role'),
(4, 'ROLE_USER', 'Regular user role');

-- Users
INSERT INTO users (id, username, email, password, first_name, last_name, role_id, city_id, created_at) VALUES 
(1, 'admin', 'admin@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'Admin', 'User', 1, 1, CURRENT_TIMESTAMP),
(2, 'hrmanager', 'hr@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'HR', 'Manager', 2, 1, CURRENT_TIMESTAMP),
(3, 'recruiter', 'recruiter@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'John', 'Recruiter', 3, 2, CURRENT_TIMESTAMP),
(4, 'user1', 'user1@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'Regular', 'User', 4, 3, CURRENT_TIMESTAMP);

-- Organizations
INSERT INTO organizations (id, name, description, domain, disabled, logo_img_src, created_date_time, last_updated_date_time) VALUES
('371960f6-087b-11f0-ad8a-16ff9fcccdb', 'Tech Solutions Inc', 'Leading technology solutions provider', 'techsolutions.com', 0, 'tech-solutions-logo.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('371960f6-087b-11f0-ad8a-16ff9fcccdc', 'Digital Innovations', 'Digital transformation company', 'digitalinnovations.com', 0, 'digital-innovations-logo.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('371960f6-087b-11f0-ad8a-16ff9fcccdd', 'Creative Works', 'Creative agency and design studio', 'creativeworks.com', 0, 'creative-works-logo.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Companies
INSERT INTO companies (id, name, description, industry, size, employee_count, founded, headquarters, organization_id) VALUES
(1, 'Tech Corp', 'Leading technology company', 'Technology', '1000-5000', 2500, 2000, 'New York, USA', '371960f6-087b-11f0-ad8a-16ff9fcccdb'),
(2, 'Web Solutions', 'Web development company', 'Technology', '100-500', 250, 2010, 'Los Angeles, USA', '371960f6-087b-11f0-ad8a-16ff9fcccdb'),
(3, 'Cloud Systems', 'Cloud infrastructure provider', 'Technology', '500-1000', 750, 2015, 'Chicago, USA', '371960f6-087b-11f0-ad8a-16ff9fcccdc'),
(4, 'Digital Products Inc', 'Digital product development', 'Technology', '100-500', 300, 2012, 'New York, USA', '371960f6-087b-11f0-ad8a-16ff9fcccdc'),
(5, 'Creative Agency', 'Creative design agency', 'Design', '50-100', 75, 2018, 'Houston, USA', '371960f6-087b-11f0-ad8a-16ff9fcccdd');

-- Jobs (Updated with company_id)
INSERT INTO jobs (id, title, description, requirements, salary_range, company_name, location_id, posted_by, status, created_at, company_id) VALUES 
(1, 'Senior Java Developer', 'We are looking for an experienced Java developer to join our team.', '- 5+ years Java experience\n- Spring Boot\n- Microservices\n- SQL databases', '100000-130000', 'Tech Corp', 1, 2, 'ACTIVE', CURRENT_TIMESTAMP, 1),
(2, 'Frontend Developer', 'Frontend developer position with React expertise', '- 3+ years React\n- TypeScript\n- CSS/SASS\n- REST APIs', '80000-100000', 'Web Solutions', 2, 2, 'ACTIVE', CURRENT_TIMESTAMP, 2),
(3, 'DevOps Engineer', 'Looking for a DevOps engineer with strong AWS experience', '- AWS\n- Docker\n- Kubernetes\n- CI/CD', '90000-120000', 'Cloud Systems', 3, 3, 'ACTIVE', CURRENT_TIMESTAMP, 3),
(4, 'Product Manager', 'Experienced product manager for our digital products team', '- 4+ years PM experience\n- Agile methodologies\n- Technical background', '110000-140000', 'Digital Products Inc', 1, 2, 'ACTIVE', CURRENT_TIMESTAMP, 4),
(5, 'UI/UX Designer', 'Creative UI/UX designer for our design team', '- Figma\n- Adobe Creative Suite\n- 3+ years experience\n- Portfolio required', '70000-90000', 'Creative Agency', 4, 3, 'ACTIVE', CURRENT_TIMESTAMP, 5); 
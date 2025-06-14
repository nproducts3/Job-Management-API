-- Users
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    role_id VARCHAR(36),
    organization_id VARCHAR(36),
    email_verified BOOLEAN DEFAULT FALSE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    last_login TIMESTAMP,
    phone_number VARCHAR(20),
    profile_picture VARCHAR(255),
    disabled BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id),
    UNIQUE (username),
    UNIQUE (email)
);

-- Organizations
CREATE TABLE IF NOT EXISTS organizations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    domain VARCHAR(100) NOT NULL,
    disabled TINYINT(1) DEFAULT 0,
    logo_img_src TEXT,
    created_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Companies
CREATE TABLE IF NOT EXISTS companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
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
    organization_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Job Resumes
CREATE TABLE IF NOT EXISTS job_resumes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    job_id INT NOT NULL,
    resume_file VARCHAR(255) NOT NULL,
    resume_text TEXT,
    match_percentage DECIMAL(5,2) DEFAULT 0.00,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id)
);

-- Update jobs table to add company_id foreign key
ALTER TABLE jobs
ADD COLUMN company_id INT,
ADD FOREIGN KEY (company_id) REFERENCES companies(id); 
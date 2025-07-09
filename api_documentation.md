# Job Portal API Documentation

## Role Endpoints

### Get All Roles
```http
GET /api/roles
Response:
[
    {
        "id": 1,
        "name": "ROLE_ADMIN"
    },
    {
        "id": 2,
        "name": "ROLE_USER"
    },
    {
        "id": 3,
        "name": "ROLE_RECRUITER"
    }
]
```

### Get Role by ID
```http
GET /api/roles/{id}
Example: GET /api/roles/1
Response:
{
    "id": 1,
    "name": "ROLE_ADMIN"
}
```

### Create Role
```http
POST /api/roles
Request:
{
    "name": "ROLE_MANAGER"
}
Response:
{
    "id": 4,
    "name": "ROLE_MANAGER"
}
```

### Update Role
```http
PUT /api/roles/{id}
Example: PUT /api/roles/4
Request:
{
    "name": "ROLE_SENIOR_MANAGER"
}
Response:
{
    "id": 4,
    "name": "ROLE_SENIOR_MANAGER"
}
```

### Delete Role
```http
DELETE /api/roles/{id}
Example: DELETE /api/roles/4
Response: 204 No Content
```

## User Endpoints

### Get All Users
```http
GET /api/users
Response:
[
    {
        "id": 1,
        "username": "john_doe",
        "email": "john.doe@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "roles": ["ROLE_USER"]
    }
]
```

### Get User by ID
```http
GET /api/users/{id}
Example: GET /api/users/1
Response:
{
    "id": 1,
    "username": "john_doe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "roles": ["ROLE_USER"]
}
```

### Create User
```http
POST /api/users
Request:
{
    "username": "jane_smith",
    "email": "jane.smith@example.com",
    "password": "securePassword123",
    "firstName": "Jane",
    "lastName": "Smith",
    "roles": ["ROLE_USER"]
}
Response:
{
    "id": 2,
    "username": "jane_smith",
    "email": "jane.smith@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "roles": ["ROLE_USER"]
}
```

### Update User
```http
PUT /api/users/{id}
Example: PUT /api/users/2
Request:
{
    "firstName": "Jane",
    "lastName": "Smith-Jones",
    "email": "jane.smith@example.com"
}
Response:
{
    "id": 2,
    "username": "jane_smith",
    "email": "jane.smith@example.com",
    "firstName": "Jane",
    "lastName": "Smith-Jones",
    "roles": ["ROLE_USER"]
}
```

### Delete User
```http
DELETE /api/users/{id}
Example: DELETE /api/users/2
Response: 204 No Content
```

## Job Endpoints

### Get All Jobs
```http
GET /api/jobs
Response:
[
    {
        "id": 1,
        "title": "Senior Software Engineer",
        "company": "Amazon",
        "department": "Engineering",
        "description": "We are looking for a skilled Senior Software Engineer...",
        "salary": "8-15 Lacs PA",
        "salaryMin": 800000,
        "salaryMax": 1500000,
        "salaryCurrency": "INR",
        "experience": "3-7 Yrs",
        "experienceMin": 3,
        "experienceMax": 7,
        "location": "Bangalore",
        "workType": "Full-time",
        "workEnvironment": "Hybrid",
        "status": "Active"
    }
]
```

### Get Job by ID
```http
GET /api/jobs/{id}
Example: GET /api/jobs/1
Response:
{
    "id": 1,
    "title": "Senior Software Engineer",
    "company": "Amazon",
    "department": "Engineering",
    "description": "We are looking for a skilled Senior Software Engineer...",
    "salary": "8-15 Lacs PA",
    "salaryMin": 800000,
    "salaryMax": 1500000,
    "salaryCurrency": "INR",
    "experience": "3-7 Yrs",
    "experienceMin": 3,
    "experienceMax": 7,
    "location": "Bangalore",
    "workType": "Full-time",
    "workEnvironment": "Hybrid",
    "status": "Active"
}
```

### Create Job
```http
POST /api/jobs
Request:
{
    "title": "Frontend Developer",
    "company": "Google",
    "department": "Engineering",
    "description": "Looking for a skilled Frontend Developer...",
    "salary": "5-8 Lacs PA",
    "salaryMin": 500000,
    "salaryMax": 800000,
    "salaryCurrency": "INR",
    "experience": "1-3 Yrs",
    "experienceMin": 1,
    "experienceMax": 3,
    "location": "Bangalore",
    "workType": "Full-time",
    "workEnvironment": "Remote",
    "status": "Active"
}
Response:
{
    "id": 2,
    "title": "Frontend Developer",
    ...
}
```

### Update Job
```http
PUT /api/jobs/{id}
Example: PUT /api/jobs/2
Request:
{
    "salary": "6-9 Lacs PA",
    "salaryMin": 600000,
    "salaryMax": 900000
}
Response:
{
    "id": 2,
    "title": "Frontend Developer",
    "salary": "6-9 Lacs PA",
    "salaryMin": 600000,
    "salaryMax": 900000,
    ...
}
```

### Delete Job
```http
DELETE /api/jobs/{id}
Example: DELETE /api/jobs/2
Response: 204 No Content
```

## City Endpoints

### Get All Cities
```http
GET /api/cities
Response:
[
    {
        "id": 1,
        "name": "Bangalore",
        "state": "Karnataka",
        "country": "India"
    }
]
```

### Get City by ID
```http
GET /api/cities/{id}
Example: GET /api/cities/1
Response:
{
    "id": 1,
    "name": "Bangalore",
    "state": "Karnataka",
    "country": "India"
}
```

### Create City
```http
POST /api/cities
Request:
{
    "name": "Mumbai",
    "state": "Maharashtra",
    "country": "India"
}
Response:
{
    "id": 2,
    "name": "Mumbai",
    "state": "Maharashtra",
    "country": "India"
}
```

### Update City
```http
PUT /api/cities/{id}
Example: PUT /api/cities/2
Request:
{
    "name": "Mumbai City",
    "state": "Maharashtra",
    "country": "India"
}
Response:
{
    "id": 2,
    "name": "Mumbai City",
    "state": "Maharashtra",
    "country": "India"
}
```

### Delete City
```http
DELETE /api/cities/{id}
Example: DELETE /api/cities/2
Response: 204 No Content
```

## Application Endpoints

### Get All Applications
```http
GET /api/applications
Response:
[
    {
        "id": 1,
        "jobId": 1,
        "userId": 1,
        "status": "Under Review",
        "coverLetter": "I am excited to apply...",
        "resumeUrl": "https://example.com/resumes/john_doe_resume.pdf",
        "expectedSalary": 1200000,
        "availableFrom": "2024-02-01",
        "notes": "I am particularly interested...",
        "recruiterNotes": null,
        "appliedAt": "2024-01-15T14:30:00",
        "updatedAt": "2024-01-15T14:30:00",
        "jobTitle": "Senior Software Engineer",
        "companyName": "Amazon",
        "userName": "john_doe",
        "userEmail": "john.doe@example.com"
    }
]
```

### Get Application by ID
```http
GET /api/applications/{id}
Example: GET /api/applications/1
Response:
{
    "id": 1,
    "jobId": 1,
    "userId": 1,
    "status": "Under Review",
    ...
}
```

### Create Application
```http
POST /api/applications
Request:
{
    "jobId": 1,
    "userId": 1,
    "status": "Applied",
    "coverLetter": "I am excited to apply...",
    "resumeUrl": "https://example.com/resumes/john_doe_resume.pdf",
    "expectedSalary": 1200000,
    "availableFrom": "2024-02-01",
    "notes": "I am particularly interested..."
}
Response:
{
    "id": 2,
    "jobId": 1,
    "userId": 1,
    "status": "Applied",
    ...
}
```

### Update Application
```http
PUT /api/applications/{id}
Example: PUT /api/applications/2
Request:
{
    "status": "Interview Scheduled",
    "recruiterNotes": "Candidate shows strong potential"
}
Response:
{
    "id": 2,
    "status": "Interview Scheduled",
    "recruiterNotes": "Candidate shows strong potential",
    ...
}
```

### Delete Application
```http
DELETE /api/applications/{id}
Example: DELETE /api/applications/2
Response: 204 No Content
```

### Additional Application Endpoints

#### Get Applications by Job ID
```http
GET /api/applications/job/{jobId}
Example: GET /api/applications/job/1
```

#### Get Applications by User ID
```http
GET /api/applications/user/{userId}
Example: GET /api/applications/user/1
```

#### Check if User has Applied
```http
GET /api/applications/check?jobId={jobId}&userId={userId}
Example: GET /api/applications/check?jobId=1&userId=1
Response: true/false
``` 

# Job Management API Documentation

## Resume Analysis Endpoints

### 1. Analyze Resume Against All Jobs
**POST** `/api/resume-analysis/analyze`

Upload a resume and get comprehensive analysis against all available Google Jobs.

**Request:**
- `file`: Resume file (PDF or DOCX)
- `jobSeekerId`: Job seeker ID

**Response:**
```json
{
  "resumeId": "uuid",
  "resumeFile": "resume.pdf",
  "resumeText": "Extracted text from resume...",
  "uploadedAt": "2025-07-08T10:30:00",
  "jobSeekerId": "job-seeker-uuid",
  "jobSeekerName": "John Doe",
  "extractedSkills": ["java", "spring", "sql", "react"],
  "skillsByCategory": {
    "programming_languages": ["java"],
    "backend": ["spring"],
    "databases": ["sql"],
    "frontend": ["react"]
  },
  "jobMatches": [
    {
      "jobId": "job-uuid",
      "jobTitle": "Senior Software Engineer",
      "companyName": "Tech Corp",
      "location": "New York",
      "matchPercentage": 85.5,
      "matchedSkills": ["java", "spring", "sql"],
      "missingSkills": ["kubernetes"],
      "categoryScores": {
        "programming_languages": 90.0,
        "backend": 85.0,
        "databases": 80.0
      },
      "jobDescription": "Job description...",
      "qualifications": "Required qualifications...",
      "responsibilities": ["Develop features", "Code review"],
      "benefits": ["Health insurance", "401k"],
      "applyLink": "https://apply.example.com",
      "salary": "$120,000",
      "scheduleType": "Full-time"
    }
  ],
  "averageMatchPercentage": 65.2,
  "totalJobsAnalyzed": 150,
  "highMatchJobs": 25,
  "mediumMatchJobs": 45,
  "lowMatchJobs": 80
}
```

### 2. Get Top Matching Jobs
**POST** `/api/resume-analysis/top-matches`

Analyze resume text and return top matching jobs.

**Request:**
- `resumeText`: Resume text content
- `limit`: Number of top matches (default: 10)

**Response:**
```json
[
  {
    "jobId": "job-uuid",
    "jobTitle": "Software Engineer",
    "companyName": "Tech Corp",
    "location": "San Francisco",
    "matchPercentage": 92.5,
    "matchedSkills": ["java", "spring", "sql", "react"],
    "missingSkills": ["docker"],
    "categoryScores": {
      "programming_languages": 95.0,
      "backend": 90.0,
      "frontend": 85.0
    }
  }
]
```

### 3. Extract Skills from Resume Text
**GET** `/api/resume-analysis/skills`

Extract and categorize skills from resume text.

**Request:**
- `resumeText`: Resume text content

**Response:**
```json
{
  "extractedSkills": ["java", "spring", "sql", "react", "javascript"],
  "skillsByCategory": {
    "programming_languages": ["java", "javascript"],
    "backend": ["spring"],
    "databases": ["sql"],
    "frontend": ["react"]
  }
}
```

## Skill Categories

The system recognizes and categorizes skills into the following categories:

### Programming Languages
- Java, Python, JavaScript, TypeScript, Ruby, PHP, C++, C#

### Frontend Development
- HTML, CSS, React, Angular, Vue, Webpack

### Backend Development
- Node.js, Express, Spring, Django, REST APIs

### Databases
- SQL, MySQL, PostgreSQL, MongoDB, Redis, Oracle

### DevOps & Cloud
- AWS, Docker, Kubernetes, Jenkins, Terraform

### Testing
- Unit testing, Integration testing, JUnit, Selenium, Cypress

### Content Writing & Marketing
- Content writing, SEO, Digital marketing, Social media, Analytics

## Match Percentage Calculation

The system calculates match percentages using:

1. **Skill Matching**: Direct skill comparison between resume and job requirements
2. **Category Weighting**: Different skill categories have different weights
3. **Missing Skills Penalty**: Penalties for missing required skills
4. **Minimum Threshold**: Jobs with <30% match are set to 0%

### Category Weights:
- Programming Languages: 1.5x
- Frontend/Backend: 1.3x
- Databases: 1.2x
- DevOps: 1.1x
- Content Writing: 1.5x
- Digital Marketing: 1.4x
- Social Media: 1.3x
- Analytics: 1.2x

## Usage Examples

### Frontend Integration (JavaScript)
```javascript
// Analyze resume against all jobs
const formData = new FormData();
formData.append('file', resumeFile);
formData.append('jobSeekerId', 'job-seeker-uuid');

const response = await fetch('/api/resume-analysis/analyze', {
  method: 'POST',
  body: formData
});

const analysis = await response.json();
console.log('Top match:', analysis.jobMatches[0]);
console.log('Average match:', analysis.averageMatchPercentage);
```

### Get Top Matches for Resume Text
```javascript
const response = await fetch('/api/resume-analysis/top-matches', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  body: new URLSearchParams({
    resumeText: 'Experienced Java developer with Spring Boot...',
    limit: '5'
  })
});

const topMatches = await response.json();
```

### Extract Skills Only
```javascript
const response = await fetch('/api/resume-analysis/skills?resumeText=' + 
  encodeURIComponent(resumeText));
const skills = await response.json();
console.log('Extracted skills:', skills.extractedSkills);
``` 
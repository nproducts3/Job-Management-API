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
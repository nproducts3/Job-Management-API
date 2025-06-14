# API Endpoints Documentation

## Cities

### GET `/api/cities` - Get all cities
Response example:
```json
[
  {
    "id": 1,
    "name": "New York",
    "country": "USA"
  },
  {
    "id": 2,
    "name": "London",
    "country": "UK"
  }
]
```

### POST `/api/cities` - Create new city
Request:
```json
{
  "name": "Sydney",
  "country": "Australia"
}
```

## Roles

### GET `/api/roles` - Get all roles
Response example:
```json
[
  {
    "id": 1,
    "name": "ROLE_ADMIN",
    "description": "Administrator role with full access"
  },
  {
    "id": 2,
    "name": "ROLE_HR",
    "description": "Human Resources role"
  }
]
```

### POST `/api/roles` - Create new role
Request:
```json
{
  "name": "ROLE_MANAGER",
  "description": "Manager role with department access"
}
```

## Users

### GET `/api/users` - Get all users
Response example:
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "firstName": "Admin",
    "lastName": "User",
    "role": {
      "id": 1,
      "name": "ROLE_ADMIN"
    },
    "city": {
      "id": 1,
      "name": "New York"
    }
  }
]
```

### POST `/api/users` - Create new user
Request:
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "securepassword",
  "firstName": "New",
  "lastName": "User",
  "roleId": 4,
  "cityId": 2
}
```

## Jobs

### GET `/api/jobs` - Get all jobs
Response example:
```json
[
  {
    "id": 1,
    "title": "Senior Java Developer",
    "description": "We are looking for an experienced Java developer to join our team.",
    "requirements": "- 5+ years Java experience\n- Spring Boot\n- Microservices\n- SQL databases",
    "salaryRange": "100000-130000",
    "companyName": "Tech Corp",
    "location": {
      "id": 1,
      "name": "New York"
    },
    "status": "ACTIVE"
  }
]
```

### POST `/api/jobs` - Create new job
Request:
```json
{
  "title": "Full Stack Developer",
  "description": "Looking for a full stack developer with React and Spring Boot experience",
  "requirements": "- 3+ years full stack experience\n- React\n- Spring Boot\n- PostgreSQL",
  "salaryRange": "90000-120000",
  "companyName": "Tech Solutions Inc",
  "locationId": 1,
  "status": "ACTIVE"
}
```

## Testing with cURL

### Example Commands

1. Get all jobs:
```bash
curl -X GET http://localhost:8080/api/jobs
```

2. Create new job:
```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Full Stack Developer",
    "description": "Looking for a full stack developer with React and Spring Boot experience",
    "requirements": "- 3+ years full stack experience\n- React\n- Spring Boot\n- PostgreSQL",
    "salaryRange": "90000-120000",
    "companyName": "Tech Solutions Inc",
    "locationId": 1,
    "status": "ACTIVE"
  }'
```

3. Update job:
```bash
curl -X PUT http://localhost:8080/api/jobs/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Senior Java Developer",
    "description": "Updated description for Java developer position",
    "requirements": "- 7+ years Java experience\n- Spring Boot\n- Microservices\n- SQL databases",
    "salaryRange": "120000-150000",
    "companyName": "Tech Corp",
    "locationId": 1,
    "status": "ACTIVE"
  }'
```

4. Delete job:
```bash
curl -X DELETE http://localhost:8080/api/jobs/1
```

Note: 
1. Replace `localhost:8080` with your actual server address if different.
2. All sample passwords in the data are hashed using BCrypt with the value "password123"
3. The sample data includes relationships between entities (e.g., users have roles and cities) 
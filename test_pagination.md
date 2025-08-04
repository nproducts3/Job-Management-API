# Testing Resume Analysis Pagination

## Step 1: Check Database Status

First, check if there are Google Jobs in the database:

```bash
curl -X GET http://localhost:8080/api/resume-analysis/database-stats
```

Expected response should show:
```json
{
  "totalGoogleJobs": 2,
  "totalJobSeekers": 2,
  "totalResumes": 0,
  "aiServiceUrl": "http://localhost:8000",
  "sampleJobs": [
    {
      "id": "b3e1c1e2-1234-4a5b-8c6d-123456790001",
      "title": "Software Engineer",
      "companyName": "Acme Corp",
      "location": "New York"
    }
  ]
}
```

## Step 2: Test Paginated Resume Analysis

### Test with Page 0, Size 10
```bash
curl -X POST http://localhost:8080/api/resume-analysis/analyze-paginated \
  -F "file=@uploads/resumes/Alice_Smith_FULL_TIME_Nikitha_Sharma_Data_Analyst_Resume.docx" \
  -F "jobSeekerId=b3e1c1e2-1234-4a5b-8c6d-12345678c001" \
  -F "page=0" \
  -F "size=10"
```

### Test with Page 0, Size 5
```bash
curl -X POST http://localhost:8080/api/resume-analysis/analyze-paginated \
  -F "file=@uploads/resumes/Alice_Smith_FULL_TIME_Nikitha_Sharma_Data_Analyst_Resume.docx" \
  -F "jobSeekerId=b3e1c1e2-1234-4a5b-8c6d-12345678c001" \
  -F "page=0" \
  -F "size=5"
```

## Step 3: Test Paginated Job Matches

```bash
curl -X POST http://localhost:8080/api/resume-analysis/top-matches-paginated \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "resumeText=Nikitha Sharma Hyderabad, India | nikitha.sharma@email.com | +91-9876543210 | LinkedIn Profile | GitHub Profile Professional Summary Detail-oriented Data Analyst with 2+ years of experience in collecting, analyzing, and visualizing large datasets to deliver actionable business insights. Proficient in SQL, Python, and Power BI, with a strong foundation in statistics and data modeling. Passionate about transforming raw data into strategic solutions that support business growth. Core Competencies • Data Cleaning & Exploration • Statistical Analysis • SQL & Relational Databases • Python (Pandas, NumPy, Matplotlib) • Excel (Pivot Tables, VLOOKUP) • Data Visualization (Power BI, Tableau) • A/B Testing • Business Intelligence & Reporting" \
  -d "jobSeekerId=b3e1c1e2-1234-4a5b-8c6d-12345678c001" \
  -d "page=0" \
  -d "size=10"
```

## Expected Results

### For Resume Analysis with Pagination:
```json
{
  "resumeId": "uuid",
  "resumeFile": "Alice_Smith_FULL_TIME_Nikitha_Sharma_Data_Analyst_Resume.docx",
  "resumeText": "...",
  "uploadedAt": "2025-07-30T12:25:15.5275969",
  "jobSeekerId": "b3e1c1e2-1234-4a5b-8c6d-12345678c001",
  "jobSeekerName": "Alice Smith",
  "extractedSkills": ["analytics", "python", "sql", "data visualization"],
  "skillsByCategory": {
    "programming_languages": ["python"],
    "databases": ["sql"],
    "analytics": ["analytics", "data visualization"]
  },
  "jobMatches": [
    {
      "googleJobId": "b3e1c1e2-1234-4a5b-8c6d-123456790002",
      "jobTitle": "Data Analyst",
      "companyName": "Globex Inc",
      "location": "San Francisco",
      "matchPercentage": 85.5,
      "matchedSkills": ["sql", "analytics"],
      "missingSkills": ["tableau"],
      "categoryScores": {
        "databases": 90.0,
        "analytics": 85.0
      }
    }
  ],
  "averageMatchPercentage": 85.5,
  "totalJobsAnalyzed": 1,
  "highMatchJobs": 1,
  "mediumMatchJobs": 0,
  "lowMatchJobs": 0,
  "currentPage": 0,
  "totalPages": 1,
  "pageSize": 10,
  "hasNextPage": false,
  "hasPreviousPage": false
}
```

### For Paginated Job Matches:
```json
{
  "content": [
    {
      "googleJobId": "b3e1c1e2-1234-4a5b-8c6d-123456790002",
      "jobTitle": "Data Analyst",
      "companyName": "Globex Inc",
      "location": "San Francisco",
      "matchPercentage": 85.5,
      "matchedSkills": ["sql", "analytics"],
      "missingSkills": ["tableau"],
      "categoryScores": {
        "databases": 90.0,
        "analytics": 85.0
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0,
  "empty": false
}
```

## Troubleshooting

### If no jobs are found:
1. Check if the database has Google Jobs:
   ```bash
   curl -X GET http://localhost:8080/api/resume-analysis/database-stats
   ```

2. If `totalGoogleJobs` is 0, you need to load sample data:
   ```sql
   -- Check if the migration V2__insert.sql was executed
   SELECT COUNT(*) FROM google_jobs;
   ```

3. If no jobs exist, run the migration:
   ```bash
   # The migration should be in src/main/resources/db/migration/V2__insert.sql
   # It should have been executed when the application started
   ```

### If AI service is not available:
The system will automatically fall back to local skill matching. You should see this in the logs:
```
WARN: AI service returned no top matches, falling back to local matching
INFO: Performing local skill matching for X jobs
INFO: Local skill matching completed, found X matches
```

### If matches are still empty:
1. Check the skill extraction logic
2. Verify that the resume text contains relevant skills
3. Check that the Google Jobs have qualifications/descriptions that match the skills

## Performance Notes

- The system now uses pagination to process only a subset of jobs at a time
- Local skill matching is used as a fallback when AI service is unavailable
- Results are sorted by match percentage (highest first)
- Pagination metadata is included in the response for frontend navigation 
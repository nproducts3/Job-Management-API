# Resume Analysis Pagination Features

## Overview

The Job Management API now supports pagination for resume analysis, allowing efficient processing of large numbers of Google Jobs. This feature helps improve performance and user experience when analyzing resumes against extensive job databases.

## New Endpoints

### 1. Paginated Resume Analysis

**POST** `/api/resume-analysis/analyze-paginated`

Upload a resume and get analysis against available jobs with pagination support.

**Request Parameters:**
- `file`: Resume file (PDF or DOCX) - **Required**
- `jobSeekerId`: Job seeker ID - **Required**
- `page`: Page number (0-based, default: 0) - **Optional**
- `size`: Page size (default: 10) - **Optional**

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
      "googleJobId": "job-uuid",
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
      }
    }
  ],
  "averageMatchPercentage": 65.2,
  "totalJobsAnalyzed": 10,
  "highMatchJobs": 3,
  "mediumMatchJobs": 4,
  "lowMatchJobs": 3
}
```

### 2. Paginated Job Matches

**POST** `/api/resume-analysis/top-matches-paginated`

Get paginated top matching jobs for resume text without file upload.

**Request Parameters:**
- `resumeText`: Resume text content - **Required**
- `jobSeekerId`: Job seeker ID - **Required**
- `page`: Page number (0-based, default: 0) - **Optional**
- `size`: Page size (default: 10) - **Optional**

**Response:**
```json
{
  "content": [
    {
      "googleJobId": "job-uuid",
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
      },
      "jobDescription": "Job description...",
      "qualifications": "Required qualifications...",
      "responsibilities": ["Develop features", "Code review"],
      "benefits": ["Health insurance", "401k"],
      "applyLink": "https://apply.example.com",
      "salary": "$120,000",
      "scheduleType": "Full-time",
      "jobSeekerId": "job-seeker-uuid",
      "jobSeekerName": "John Doe"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 150,
  "totalPages": 15,
  "last": false,
  "first": true,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "empty": false
}
```

## Usage Examples

### Frontend Integration (JavaScript)

#### Paginated Resume Analysis
```javascript
// Analyze resume with pagination
const formData = new FormData();
formData.append('file', resumeFile);
formData.append('jobSeekerId', 'job-seeker-uuid');
formData.append('page', '0');
formData.append('size', '20');

const response = await fetch('/api/resume-analysis/analyze-paginated', {
  method: 'POST',
  body: formData
});

const analysis = await response.json();
console.log('Page 1 results:', analysis.jobMatches);
console.log('Total jobs analyzed:', analysis.totalJobsAnalyzed);
```

#### Paginated Job Matches
```javascript
// Get paginated job matches for resume text
const response = await fetch('/api/resume-analysis/top-matches-paginated', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  body: new URLSearchParams({
    resumeText: 'Experienced Java developer with Spring Boot...',
    jobSeekerId: 'job-seeker-uuid',
    page: '1',
    size: '15'
  })
});

const paginatedResults = await response.json();
console.log('Page 2 results:', paginatedResults.content);
console.log('Total pages:', paginatedResults.totalPages);
console.log('Total jobs:', paginatedResults.totalElements);
```

### cURL Examples

#### Paginated Resume Analysis
```bash
curl -X POST http://localhost:8080/api/resume-analysis/analyze-paginated \
  -F "file=@resume.pdf" \
  -F "jobSeekerId=123e4567-e89b-12d3-a456-426614174000" \
  -F "page=0" \
  -F "size=20"
```

#### Paginated Job Matches
```bash
curl -X POST http://localhost:8080/api/resume-analysis/top-matches-paginated \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "resumeText=Experienced Java developer with Spring Boot experience..." \
  -d "jobSeekerId=123e4567-e89b-12d3-a456-426614174000" \
  -d "page=1" \
  -d "size=15"
```

## Benefits of Pagination

### 1. Performance Improvement
- **Reduced Memory Usage**: Only loads a subset of jobs at a time
- **Faster Response Times**: Processes fewer jobs per request
- **Better Scalability**: Handles large job databases efficiently

### 2. User Experience
- **Progressive Loading**: Users can browse through jobs page by page
- **Responsive UI**: No long loading times for large datasets
- **Flexible Navigation**: Users can jump to specific pages

### 3. Resource Management
- **Database Efficiency**: Reduces database query load
- **Network Optimization**: Smaller response payloads
- **Server Load Distribution**: Spreads processing across multiple requests

## Implementation Details

### Service Layer Changes

The `JobResumeService` now includes:

1. **`analyzeResumeAgainstAllJobs(file, jobSeekerId, page, size)`**: Overloaded method with pagination support
2. **`getPaginatedJobMatches(resumeText, jobSeekerId, page, size)`**: New method for paginated job matching

### Pagination Logic

- **Page Calculation**: Uses Spring Data's `PageRequest.of(page, size)`
- **Job Processing**: Processes only jobs in the current page
- **Match Sorting**: Results are sorted by match percentage (highest first)
- **Metadata Preservation**: Maintains pagination metadata for frontend navigation

### Error Handling

- **Invalid Page Numbers**: Returns empty results for out-of-bounds pages
- **Invalid Page Sizes**: Uses default size (10) for invalid values
- **Missing Parameters**: Uses sensible defaults for optional parameters

## Migration Guide

### For Existing Users

1. **Backward Compatibility**: Original endpoints still work
2. **Gradual Migration**: Can adopt pagination incrementally
3. **Performance Testing**: Compare response times before/after

### For New Implementations

1. **Start with Pagination**: Use paginated endpoints from the beginning
2. **Optimize Page Size**: Choose appropriate page sizes based on use case
3. **Implement Caching**: Cache results for better performance

## Best Practices

### 1. Page Size Selection
- **Small Pages (5-10)**: For mobile applications or slow networks
- **Medium Pages (10-25)**: For desktop applications
- **Large Pages (25-50)**: For admin interfaces or bulk operations

### 2. Caching Strategy
- **Cache by Page**: Cache results for each page separately
- **Cache Invalidation**: Clear cache when job data changes
- **TTL Settings**: Set appropriate time-to-live for cached data

### 3. Error Handling
- **Graceful Degradation**: Fall back to non-paginated endpoints if needed
- **User Feedback**: Inform users about pagination status
- **Retry Logic**: Implement retry for failed pagination requests

## Future Enhancements

### Planned Features
1. **Filtering**: Add filters for job location, salary, company, etc.
2. **Sorting**: Allow custom sorting by different criteria
3. **Search**: Implement full-text search within paginated results
4. **Real-time Updates**: WebSocket support for live job updates

### Performance Optimizations
1. **Database Indexing**: Optimize database queries for pagination
2. **Result Caching**: Implement Redis caching for frequent queries
3. **Async Processing**: Background processing for large datasets
4. **CDN Integration**: Cache static content for faster delivery 
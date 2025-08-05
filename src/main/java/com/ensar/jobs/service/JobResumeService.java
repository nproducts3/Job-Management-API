

package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobResumeDTO;

// import java.util.Optional;
// import jakarta.persistence.EntityNotFoundException;
import com.ensar.jobs.dto.JobMatchResultDTO;
import com.ensar.jobs.dto.ResumeAnalysisDTO;
import com.ensar.jobs.entity.GoogleJob;
import com.ensar.jobs.entity.JobResume;
import com.ensar.jobs.entity.JobSeeker;
import com.ensar.jobs.repository.GoogleJobRepository;
import com.ensar.jobs.repository.JobResumeRepository;
import com.ensar.jobs.repository.JobSeekerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.persistence.EntityNotFoundException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ensar.jobs.controller.JobResumeController.AutoImproveRequest;
import com.ensar.jobs.controller.JobResumeController.AutoImproveResponse;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobResumeService {

    /**
     * Fetch paginated analysis results for a job seeker (no file required)
     */
    public ResumeAnalysisDTO getPaginatedAnalysisForJobSeeker(String jobSeekerId, int page, int size) {
        // Validate job seeker
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + jobSeekerId));

        // Fetch paginated JobResume records for the latest analyzed jobs
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        Page<JobResume> jobResumePage = jobResumeRepository.findByJobSeeker_Id(jobSeekerId, pageable);
        List<JobResume> jobResumes = jobResumePage.getContent();

        // Map JobResume to JobMatchResultDTO
        List<JobMatchResultDTO> jobMatches = jobResumes.stream().map(jr -> {
            JobMatchResultDTO dto = new JobMatchResultDTO();
            dto.setGoogleJobId(jr.getGooglejobId() != null ? UUID.fromString(jr.getGooglejobId()) : null);
            // dto.setJobTitle(jr.getJobTitle());
            // dto.setCompanyName(jr.getCompanyName());
            dto.setMatchPercentage(jr.getMatchPercentage());
            dto.setAiSuggestions(jr.getAiSuggestions());
            // Map other fields as needed
            return dto;
        }).collect(Collectors.toList());

        // Calculate average match percentage for paginated jobs
        BigDecimal averageMatchPercentage = jobMatches.isEmpty() ? BigDecimal.ZERO :
            jobMatches.stream()
                .map(JobMatchResultDTO::getMatchPercentage)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(jobMatches.size()), 2, RoundingMode.HALF_UP);

        int totalPages = jobResumePage.getTotalPages();
        boolean hasNextPage = jobResumePage.hasNext();
        boolean hasPreviousPage = jobResumePage.hasPrevious();

        return ResumeAnalysisDTO.builder()
            .jobSeekerId(jobSeekerId)
            .jobSeekerName(jobSeeker.getFirstName() + " " + jobSeeker.getLastName())
            .jobMatches(jobMatches)
            .averageMatchPercentage(averageMatchPercentage)
            .totalJobsAnalyzed((int) jobResumePage.getTotalElements())
            .currentPage(page)
            .totalPages(totalPages)
            .pageSize(size)
            .hasNextPage(hasNextPage)
            .hasPreviousPage(hasPreviousPage)
            .build();
    }

    // Ensure proper JSON serialization/deserialization for aiSuggestions
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    /**
     * Fetch resumeText and matchPercentage for a given jobSeekerId and googleJobId
     */
    public JobResumeDTO getResumeTextAndMatchPercentage(String jobSeekerId, String googleJobId) {
        List<JobResume> jobResumes = jobResumeRepository.findByJobSeeker_IdAndGooglejobId(jobSeekerId, googleJobId);
        if (jobResumes == null || jobResumes.isEmpty()) {
            throw new jakarta.persistence.EntityNotFoundException("Resume not found for jobSeekerId: " + jobSeekerId + " and googleJobId: " + googleJobId);
        }
        // If multiple exist, return the latest by uploadedAt
        jobResumes.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        return mapToDTO(jobResumes.get(0));
    }
    private final GoogleJobRepository googleJobRepository;
    private final JobResumeRepository jobResumeRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final String UPLOAD_DIR = "uploads/resumes";

    // Enhanced technical skill variations with categories and synonyms
    private static final Map<String, Set<String>> TECHNICAL_SKILL_VARIATIONS = new HashMap<>();
    static {
        // Content Writing Skills
        TECHNICAL_SKILL_VARIATIONS.put("content_writing", Set.of(
            "content writing", "blog writing", "seo content", "creative copywriting", 
            "proofreading", "editing", "storytelling", "content creation"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("digital_marketing", Set.of(
            "digital marketing", "seo", "sem", "email campaigns", "content marketing",
            "lead generation", "google ads", "marketing strategy"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("social_media", Set.of(
            "social media", "social media strategy", "community management",
            "influencer marketing", "social media marketing"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("analytics", Set.of(
            "analytics", "google analytics", "social media insights", 
            "performance tracking", "data analysis", "a/b testing"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("creativity", Set.of(
            "visual content", "brand storytelling", "design thinking",
            "brainstorming", "concept development"
        ));

        // Programming Languages
        TECHNICAL_SKILL_VARIATIONS.put("java", Set.of(
            "java", "j2ee", "java ee", "core java", "java core", "java programming"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("python", Set.of(
            "python", "python3", "python 3", "py", "python programming"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("javascript", Set.of(
            "javascript", "js", "es6", "es2015+", "ecmascript", "vanilla javascript"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("typescript", Set.of(
            "typescript", "ts", "type script"
        ));

        // Frontend Development
        TECHNICAL_SKILL_VARIATIONS.put("react", Set.of(
            "react", "react.js", "reactjs", "react js", "react native"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("angular", Set.of(
            "angular", "angular.js", "angularjs", "angular 2+", "ng"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("vue", Set.of(
            "vue", "vue.js", "vuejs", "vue 3"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("html", Set.of(
            "html", "html5", "html 5", "xhtml"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("css", Set.of(
            "css", "css3", "scss", "sass", "less", "stylesheets"
        ));

        // Backend Development
        TECHNICAL_SKILL_VARIATIONS.put("nodejs", Set.of(
            "node.js", "nodejs", "node js", "node"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("spring", Set.of(
            "spring", "spring boot", "spring framework", "spring mvc", "spring cloud"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("express", Set.of(
            "express.js", "expressjs", "express", "express js"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("rest", Set.of(
            "rest", "rest api", "restful", "restful api", "rest apis", "web services"
        ));

        // Databases
        TECHNICAL_SKILL_VARIATIONS.put("sql", Set.of(
            "sql", "mysql", "postgresql", "postgres", "oracle", "sql server", "tsql", "plsql"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("mongodb", Set.of(
            "mongodb", "mongo", "mongoose", "nosql"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("redis", Set.of(
            "redis", "redis cache", "redis db"
        ));

        // DevOps & Cloud
        TECHNICAL_SKILL_VARIATIONS.put("aws", Set.of(
            "aws", "amazon web services", "ec2", "s3", "lambda", "cloudfront", "route53"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("docker", Set.of(
            "docker", "containerization", "docker compose", "docker-compose"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("kubernetes", Set.of(
            "kubernetes", "k8s", "kubectl", "k3s"
        ));

        // Testing
        TECHNICAL_SKILL_VARIATIONS.put("testing", Set.of(
            "unit testing", "integration testing", "e2e testing", "test driven development",
            "junit", "testng", "pytest", "jest", "mocha", "jasmine", "cypress", "selenium"
        ));
        // Add more variations for common skills
        TECHNICAL_SKILL_VARIATIONS.put("frontend", Set.of(
            "frontend", "frontend frameworks", "frontend technologies"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("backend", Set.of(
            "backend", "backend technologies", "backend frameworks"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("devops", Set.of(
            "devops", "devops tools"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("analytics", Set.of(
            "analytics", "analytics platforms", "reporting tools"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("content_writing", Set.of(
            "content writing platforms"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("digital_marketing", Set.of(
            "digital marketing", "digital marketing platforms"
        ));
        TECHNICAL_SKILL_VARIATIONS.put("social_media", Set.of(
            "social media", "social media platforms"
        ));
    }

    public JobResumeDTO uploadAndProcessResume(MultipartFile file, UUID googleJobId) throws IOException {
        try {
            if (googleJobId == null) {
                throw new IllegalArgumentException("Invalid Google Job ID (googleJobId) format.");
            }

            // Delete existing resume(s) for this job
            List<JobResume> existingResumes = jobResumeRepository.findByGooglejobId(googleJobId.toString());
            for (JobResume existingResume : existingResumes) {
                Path existingFilePath = Paths.get(UPLOAD_DIR, existingResume.getResumeFile());
                try {
                    Files.deleteIfExists(existingFilePath);
                    log.info("Deleted existing resume file: {}", existingResume.getResumeFile());
                } catch (IOException e) {
                    log.warn("Failed to delete existing resume file: {}", existingResume.getResumeFile(), e);
                }
                jobResumeRepository.delete(existingResume);
                log.info("Deleted existing resume record from database");
            }

            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            // Fetch GoogleJob for job title and company name
            GoogleJob googleJob = googleJobRepository.findById(googleJobId.toString())
                .orElse(null);
            String jobTitle = googleJob != null ? googleJob.getTitle() : null;
            String companyName = googleJob != null ? googleJob.getCompanyName() : null;

            // Get job seeker for file naming
            JobSeeker jobSeeker = null;
            if (googleJob != null && googleJob.getId() != null) {
                // Try to find job seeker by job id (if available in your logic)
                // Otherwise, you may need to pass jobSeekerId as a parameter
            }
            // If jobSeeker is not found above, try to get from JobResumeDTO or other logic
            // For now, let's use the jobSeekerRepository to get a sample user (update as needed)
            // You may want to pass jobSeekerId to this method for accuracy
            // Example fallback:
            jobSeeker = jobSeekerRepository.findAll().stream().findFirst().orElse(null);
            String safeFirstName = jobSeeker != null ? jobSeeker.getFirstName().replaceAll("[^a-zA-Z0-9]", "_") : "user";
            String safeLastName = jobSeeker != null ? jobSeeker.getLastName().replaceAll("[^a-zA-Z0-9]", "_") : "unknown";
            String filename = safeFirstName + "_" + safeLastName + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                log.info("File saved successfully: {}", filePath);
            } catch (IOException e) {
                log.error("Failed to save uploaded file: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to save uploaded file", e);
            }
            // Extract text from the resume file
            String resumeText = extractTextFromFile(filePath.toFile());
            log.info("Successfully extracted text from resume, length: {} characters", resumeText.length());

            // Extract job skills from GoogleJob if available
            Set<String> jobSkills = googleJob != null ? extractJobSkills(googleJob) : new HashSet<>();
            Map<String, Set<String>> resumeSkillsByCategory = extractSkillsByCategory(resumeText);
            Set<String> resumeSkills = resumeSkillsByCategory.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
            SkillMatchResult matchResult = calculateDetailedSkillMatch(jobSkills, resumeSkills, resumeSkillsByCategory);

            // Example: aiSuggestions should be generated by your AI logic, here we use a placeholder
            java.util.List<com.ensar.jobs.dto.AiSuggestion> aiSuggestionsList = new java.util.ArrayList<>();
            aiSuggestionsList.add(new com.ensar.jobs.dto.AiSuggestion("example_type", "This is an example suggestion."));
            String aiSuggestionsJson = null;
            try {
                aiSuggestionsJson = objectMapper.writeValueAsString(aiSuggestionsList);
            } catch (Exception e) {
                aiSuggestionsJson = null;
            }

            JobResume jobResume = JobResume.builder()
                    .id(UUID.randomUUID().toString())
                    .googlejobId(googleJobId.toString())
                    .resumeFile(filename)
                    .resumeText(resumeText)
                    .matchPercentage(matchResult.getMatchPercentage())
                    .uploadedAt(LocalDateTime.now())
                    .aiSuggestions(aiSuggestionsJson)
                    .build();

            jobResume = jobResumeRepository.save(jobResume);
            log.info("Successfully saved job resume with ID: {}", jobResume.getId());

            return JobResumeDTO.builder()
                    .id(jobResume.getId())
                    .googleJobId(googleJobId.toString())
                    .resumeFile(filename)
                    .resumeText(resumeText)
                    .matchPercentage(matchResult.getMatchPercentage())
                    .uploadedAt(jobResume.getUploadedAt())
                    .jobTitle(jobTitle)
                    .companyName(companyName)
                    .matchedSkills(matchResult.getMatchedSkills() != null ? String.join(", ", matchResult.getMatchedSkills()) : null)
                    .missingSkills(matchResult.getMissingSkills() != null ? String.join(", ", matchResult.getMissingSkills()) : null)
                    .aiSuggestions(aiSuggestionsList)
                    .build();
        } catch (Exception e) {
            log.error("Error processing resume upload", e);
            throw e;
        }
    }

    private String extractTextFromFile(java.io.File savedFile) throws IOException {
        String fileName = savedFile.getName().toLowerCase();
        log.info("Processing file: {}", fileName);

        String extractedText;
        if (fileName.endsWith(".pdf")) {
            extractedText = extractTextFromPDF(savedFile);
        } else if (fileName.endsWith(".docx")) {
            extractedText = extractTextFromDOCX(savedFile);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }

        if (extractedText == null || extractedText.trim().isEmpty()) {
            log.error("Failed to extract text from file: {}", savedFile.getName());
            throw new IOException("No text could be extracted from the file");
        }

        log.info("Extracted text length: {}", extractedText.length());
        log.info("First 200 characters of extracted text: {}", 
            extractedText.length() > 200 ? extractedText.substring(0, 200) : extractedText);
        
        return extractedText;
    }

    private Set<String> extractJobSkills(GoogleJob googleJob) {
        Set<String> skills = new HashSet<>();
        // Extract skills from responsibilities, benefits, qualifications, description
        try {
            if (googleJob.getResponsibilities() != null && !googleJob.getResponsibilities().isEmpty()) {
                skills.addAll(googleJob.getResponsibilities().stream().map(String::toLowerCase).collect(Collectors.toSet()));
            }
            if (googleJob.getBenefits() != null && !googleJob.getBenefits().isEmpty()) {
                skills.addAll(googleJob.getBenefits().stream().map(String::toLowerCase).collect(Collectors.toSet()));
            }
            if (googleJob.getQualifications() != null) {
                skills.addAll(extractSkillsFromText(googleJob.getQualifications()));
            }
            if (googleJob.getDescription() != null) {
                skills.addAll(extractSkillsFromText(googleJob.getDescription()));
            }
        } catch (Exception e) {
            log.warn("Error extracting skills from GoogleJob fields", e);
        }
        return skills;
    }

    public Set<String> extractSkillsFromText(String text) {
        Set<String> foundSkills = new HashSet<>();
        // Remove markdown formatting and normalize text
        String normalizedText = text
            .replaceAll("\\*\\*", "") // remove bold
            .replaceAll("[\\*_•\\-]", " ") // remove bullets, asterisks, dashes
            .replaceAll("\\s+", " ") // collapse whitespace
            .toLowerCase();

        // Extract skills from lines starting with bullets or colons
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String cleanLine = line.trim().replaceAll("\\*\\*", "").replaceAll("[\\*_•\\-]", " ").toLowerCase();
            // If line contains a colon, treat as category: skills
            if (cleanLine.contains(":")) {
                String[] parts = cleanLine.split(":", 2);
                if (parts.length == 2) {
                    String[] skills = parts[1].split(",");
                    for (String skill : skills) {
                        processSkill(skill.trim(), foundSkills);
                    }
                }
            } else {
                // Otherwise, treat the whole line as a possible skill
                processSkill(cleanLine, foundSkills);
            }
        }

        // Also run the original extraction logic on the normalized text
        // First look for skills section
        Pattern skillSectionPattern = Pattern.compile(
            "(?i)(skills|expertise|competencies|qualifications|proficiencies)[:\\s]*([^\\n]*(?:\\n(?!\\n)[^\\n]*)*)"
        );
        Matcher sectionMatcher = skillSectionPattern.matcher(normalizedText);
        while (sectionMatcher.find()) {
            String skillsSection = sectionMatcher.group(2);
            String[] skills = skillsSection.split("[,;•|/\\n]+");
            for (String skill : skills) {
                processSkill(skill.trim(), foundSkills);
            }
        }

        // Look for skills in bullet points
        Pattern bulletPattern = Pattern.compile("(?m)^[•\\-\\*]\\s*(.+)$");
        Matcher bulletMatcher = bulletPattern.matcher(normalizedText);
        while (bulletMatcher.find()) {
            String bulletPoint = bulletMatcher.group(1).trim();
            processSkill(bulletPoint, foundSkills);
        }

        // Look for skills in the general text
        for (Map.Entry<String, Set<String>> entry : TECHNICAL_SKILL_VARIATIONS.entrySet()) {
            for (String variation : entry.getValue()) {
                if (normalizedText.contains(variation)) {
                    foundSkills.add(entry.getKey());
                }
            }
        }

        return foundSkills;
    }

    private void processSkill(String text, Set<String> foundSkills) {
        String normalizedText = text.toLowerCase().replaceAll("\\(.*?\\)", "").trim(); // Remove parentheticals
        
        // Check for direct matches in our skill variations
        for (Map.Entry<String, Set<String>> entry : TECHNICAL_SKILL_VARIATIONS.entrySet()) {
            for (String variation : entry.getValue()) {
                if (normalizedText.contains(variation)) {
                    foundSkills.add(entry.getKey());
                }
            }
        }

        // Check for specific content writing and marketing phrases
        Pattern contentPattern = Pattern.compile(
            "(?i)(content\\s*writing|blog\\s*writing|seo\\s*content|copywriting|proofreading|editing|storytelling|" +
            "digital\\s*marketing|seo|sem|email\\s*campaigns|content\\s*marketing|lead\\s*generation|google\\s*ads|" +
            "social\\s*media|community\\s*management|influencer\\s*marketing|" +
            "google\\s*analytics|data\\s*analysis|a/b\\s*testing|" +
            "visual\\s*content|brand\\s*storytelling|design\\s*thinking|brainstorming)"
        );
        
        Matcher contentMatcher = contentPattern.matcher(normalizedText);
        while (contentMatcher.find()) {
            String foundSkill = contentMatcher.group(1).toLowerCase();
            // Map the found skill to our standardized categories
            for (Map.Entry<String, Set<String>> entry : TECHNICAL_SKILL_VARIATIONS.entrySet()) {
                if (entry.getValue().stream().anyMatch(variation -> 
                    foundSkill.contains(variation) || variation.contains(foundSkill))) {
                    foundSkills.add(entry.getKey());
                    break;
                }
            }
        }
    }

    public Map<String, Set<String>> extractSkillsByCategory(String text) {
        Map<String, Set<String>> skillsByCategory = new HashMap<>();
        

        // Group skills by common categories
        Map<String, String> skillToCategory = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : TECHNICAL_SKILL_VARIATIONS.entrySet()) {
            String skill = entry.getKey();
            
            // Determine category based on skill patterns
            String category;
            if (skill.matches(".*(java|python|javascript|typescript|ruby|php|cpp|csharp).*")) {
                category = "programming_languages";
            } else if (skill.matches(".*(html|css|react|angular|vue|webpack).*")) {
                category = "frontend";
            } else if (skill.matches(".*(node|express|spring|django|rest|api).*")) {
                category = "backend";
            } else if (skill.matches(".*(sql|mongo|redis|postgres|oracle).*")) {
                category = "databases";
            } else if (skill.matches(".*(aws|docker|kubernetes|jenkins|terraform).*")) {
                category = "devops";
            } else if (skill.matches(".*(test|junit|selenium|cypress).*")) {
                category = "testing";
            } else {
                category = "other";
            }
            
            skillToCategory.put(skill, category);
        }

        // Extract skills and categorize them
        Set<String> foundSkills = extractSkillsFromText(text);
        for (String skill : foundSkills) {
            String category = skillToCategory.getOrDefault(skill, "other");
            skillsByCategory.computeIfAbsent(category, k -> new HashSet<>()).add(skill);
        }

        return skillsByCategory;
    }

    private static class SkillMatchResult {
        private final BigDecimal matchPercentage;
        private final Set<String> matchedSkills;
        private final Set<String> missingSkills;
        private final Map<String, BigDecimal> categoryScores;

        public SkillMatchResult(BigDecimal matchPercentage, Set<String> matchedSkills, Set<String> missingSkills, Map<String, BigDecimal> categoryScores) {
            this.matchPercentage = matchPercentage;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
            this.categoryScores = categoryScores;
        }

        public BigDecimal getMatchPercentage() { return matchPercentage; }
        public Set<String> getMatchedSkills() { return matchedSkills; }
        public Set<String> getMissingSkills() { return missingSkills; }
        public Map<String, BigDecimal> getCategoryScores() { return categoryScores; }
    }

    private SkillMatchResult calculateDetailedSkillMatch(
            Set<String> jobSkills, 
            Set<String> resumeSkills,
            Map<String, Set<String>> resumeSkillsByCategory) {
        
        if (jobSkills.isEmpty()) {
            log.warn("No job skills to match against");
            return new SkillMatchResult(
                BigDecimal.ZERO, 
                new HashSet<>(), 
                new HashSet<>(),
                new HashMap<>()
            );
        }

        // Calculate matched and missing skills
        Set<String> matchedSkills = new HashSet<>(jobSkills);
        matchedSkills.retainAll(resumeSkills);

        Set<String> missingSkills = new HashSet<>(jobSkills);
        missingSkills.removeAll(matchedSkills);

        // Calculate category-wise scores
        Map<String, BigDecimal> categoryScores = new HashMap<>();
        
        // Group job skills by category
        Map<String, Set<String>> jobSkillsByCategory = new HashMap<>();
        for (String skill : jobSkills) {
            for (Map.Entry<String, Set<String>> entry : TECHNICAL_SKILL_VARIATIONS.entrySet()) {
                if (entry.getValue().contains(skill.toLowerCase())) {
                    String category = entry.getKey();
                    jobSkillsByCategory.computeIfAbsent(category, k -> new HashSet<>()).add(skill);
                }
            }
        }

        // Calculate score for each category
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        
        for (Map.Entry<String, Set<String>> entry : jobSkillsByCategory.entrySet()) {
            String category = entry.getKey();
            Set<String> requiredSkills = entry.getValue();
            Set<String> matchedCategorySkills = new HashSet<>(requiredSkills);
            matchedCategorySkills.retainAll(resumeSkills);

            // Calculate category score based on matched vs required skills
            if (!requiredSkills.isEmpty()) {
                BigDecimal categoryScore = BigDecimal.valueOf(matchedCategorySkills.size())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(requiredSkills.size()), 2, RoundingMode.HALF_UP);
                
                // Apply stricter scoring - if less than 50% skills match in a category, reduce score significantly
                if (categoryScore.compareTo(BigDecimal.valueOf(50)) < 0) {
                    categoryScore = categoryScore.multiply(BigDecimal.valueOf(0.5));
                }
                
                categoryScores.put(category, categoryScore);

                // Add weighted score to total
                BigDecimal weight = getSkillCategoryWeight(category);
                totalScore = totalScore.add(categoryScore.multiply(weight));
                totalWeight = totalWeight.add(weight);
            }
        }

        // Calculate overall match percentage
        BigDecimal matchPercentage;
        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            matchPercentage = totalScore.divide(totalWeight, 2, RoundingMode.HALF_UP);
        } else {
            matchPercentage = BigDecimal.valueOf(matchedSkills.size())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(Math.max(1, jobSkills.size())), 2, RoundingMode.HALF_UP);
        }

        // Apply stricter penalties for missing skills
        if (!missingSkills.isEmpty()) {
            BigDecimal missingSkillsPenalty = BigDecimal.valueOf(missingSkills.size())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(Math.max(1, jobSkills.size())), 2, RoundingMode.HALF_UP);
            
            // Increase penalty weight to 60%
            BigDecimal penaltyWeight = BigDecimal.valueOf(0.6);
            matchPercentage = matchPercentage.multiply(BigDecimal.ONE.subtract(penaltyWeight))
                .add(BigDecimal.valueOf(100).subtract(missingSkillsPenalty).multiply(penaltyWeight));
        }

        // If match percentage is below 30%, set it to 0
        if (matchPercentage.compareTo(BigDecimal.valueOf(30)) < 0) {
            matchPercentage = BigDecimal.ZERO;
        }

        // Ensure the percentage is between 0 and 100
        matchPercentage = matchPercentage.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));

        return new SkillMatchResult(matchPercentage, matchedSkills, missingSkills, categoryScores);
    }

    private BigDecimal getSkillCategoryWeight(String category) {
        // Updated weights for content and marketing skills
        return switch (category.toLowerCase()) {
            case "content_writing" -> BigDecimal.valueOf(1.5);
            case "digital_marketing" -> BigDecimal.valueOf(1.4);
            case "social_media" -> BigDecimal.valueOf(1.3);
            case "analytics" -> BigDecimal.valueOf(1.2);
            case "creativity" -> BigDecimal.valueOf(1.1);
            // Technical skills weights
            case "programming_languages" -> BigDecimal.valueOf(1.5);
            case "frontend", "backend" -> BigDecimal.valueOf(1.3);
            case "databases" -> BigDecimal.valueOf(1.2);
            case "devops" -> BigDecimal.valueOf(1.1);
            default -> BigDecimal.ONE;
        };
    }
    

    private String extractTextFromPDF(java.io.File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("PDF text extraction successful, pages: {}", document.getNumberOfPages());
            return text;
        } catch (Exception e) {
            log.error("Error extracting text from PDF: {}", e.getMessage(), e);
            throw new IOException("Failed to extract text from PDF", e);
        }
    }

    private String extractTextFromDOCX(java.io.File file) throws IOException {
        try (var fis = Files.newInputStream(file.toPath());
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText();
            log.info("DOCX text extraction successful, paragraphs: {}", document.getParagraphs().size());
            return text;
        } catch (Exception e) {
            log.error("Error extracting text from DOCX: {}", e.getMessage(), e);
            throw new IOException("Failed to extract text from DOCX", e);
        }
    }

    public List<JobResumeDTO> getResumesByJobId(UUID googleJobId) {
        List<JobResume> resumes = jobResumeRepository.findByGooglejobId(googleJobId.toString());
        GoogleJob googleJob = googleJobRepository.findById(googleJobId.toString()).orElse(null);
        String jobTitle = googleJob != null ? googleJob.getTitle() : null;
        String companyName = googleJob != null ? googleJob.getCompanyName() : null;
        Set<String> jobSkills = googleJob != null ? extractJobSkills(googleJob) : new HashSet<>();
        return resumes.stream().map(resume -> {
            JobResumeDTO dto = new JobResumeDTO();
            dto.setId(resume.getId());
            dto.setGoogleJobId(googleJobId.toString());
            dto.setResumeFile(resume.getResumeFile());
            dto.setResumeText(resume.getResumeText());
            dto.setMatchPercentage(resume.getMatchPercentage());
            dto.setUploadedAt(resume.getUploadedAt());
            dto.setJobTitle(jobTitle);
            dto.setCompanyName(companyName);
            // Skill matching for each resume
            Set<String> resumeSkills = extractSkillsFromText(resume.getResumeText());
            Set<String> matchedSkills = new HashSet<>(jobSkills);
            matchedSkills.retainAll(resumeSkills);
            Set<String> missingSkills = new HashSet<>(jobSkills);
            missingSkills.removeAll(matchedSkills);
            dto.setMatchedSkills(matchedSkills.isEmpty() ? null : String.join(", ", matchedSkills));
            dto.setMissingSkills(missingSkills.isEmpty() ? null : String.join(", ", missingSkills));
            return dto;
        }).collect(Collectors.toList());
    }

    public void deleteResumeForJob(UUID googleJobId) {
        List<JobResume> existingResumes = jobResumeRepository.findByGooglejobId(googleJobId.toString());
        for (JobResume existingResume : existingResumes) {
            Path existingFilePath = Paths.get(UPLOAD_DIR, existingResume.getResumeFile());
            try {
                Files.deleteIfExists(existingFilePath);
                log.info("Deleted resume file: {}", existingResume.getResumeFile());
            } catch (IOException e) {
                log.warn("Failed to delete resume file: {}", existingResume.getResumeFile(), e);
            }
            jobResumeRepository.delete(existingResume);
            log.info("Deleted resume record from database for job: {}", googleJobId);
        }
    }

    // Add this method for mapping DTO to entity
    private JobResume mapToEntity(JobResumeDTO dto, JobSeeker jobSeeker) {
        JobResume entity = new JobResume();
        entity.setId(dto.getId());
        entity.setGooglejobId(dto.getGoogleJobId()); // DTO now should use String
        entity.setResumeFile(dto.getResumeFile());
        entity.setResumeText(dto.getResumeText());
        entity.setMatchPercentage(dto.getMatchPercentage());
        entity.setUploadedAt(dto.getUploadedAt());
        entity.setJobSeeker(jobSeeker);
        try {
            entity.setAiSuggestions(dto.getAiSuggestions() != null ? objectMapper.writeValueAsString(dto.getAiSuggestions()) : null);
        } catch (Exception e) {
            entity.setAiSuggestions(null);
        }
        return entity;
    }

    // Add this method for mapping entity to DTO
    private JobResumeDTO mapToDTO(JobResume entity) {
        JobResumeDTO dto = new JobResumeDTO();
        dto.setId(entity.getId());
        dto.setGoogleJobId(entity.getGooglejobId()); // DTO now should use String
        dto.setResumeFile(entity.getResumeFile());
        dto.setResumeText(entity.getResumeText());
        dto.setMatchPercentage(entity.getMatchPercentage());
        dto.setUploadedAt(entity.getUploadedAt());
        dto.setJobSeekerId(entity.getJobSeeker() != null ? entity.getJobSeeker().getId() : null);
        try {
            String aiSuggestionsJson = entity.getAiSuggestions();
            if (aiSuggestionsJson != null && !aiSuggestionsJson.isEmpty()) {
                // Try parsing as List<AiSuggestion> first
                List<com.ensar.jobs.dto.AiSuggestion> aiSuggestionList = null;
                try {
                    aiSuggestionList = objectMapper.readValue(aiSuggestionsJson, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.ensar.jobs.dto.AiSuggestion>>() {});
                } catch (Exception parseAsObjectList) {
                    // If fails, parse as List<String> and convert
                    List<String> suggestions = objectMapper.readValue(aiSuggestionsJson, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});
                    aiSuggestionList = suggestions.stream()
                        .map(s -> new com.ensar.jobs.dto.AiSuggestion(null, s))
                        .collect(java.util.stream.Collectors.toList());
                }
                dto.setAiSuggestions(aiSuggestionList);
            }
        } catch (Exception e) {
            dto.setAiSuggestions(null);
        }
        return dto;
    }

    // Example for create method
    public JobResumeDTO createJobResume(JobResumeDTO dto) {
        if (dto.getJobSeekerId() == null) {
            throw new IllegalArgumentException("jobSeekerId is required");
        }
        JobSeeker jobSeeker = jobSeekerRepository.findById(dto.getJobSeekerId())
            .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + dto.getJobSeekerId()));
        JobResume entity = mapToEntity(dto, jobSeeker);
        entity = jobResumeRepository.save(entity);
        return mapToDTO(entity);
    }

    private static final String AI_ANALYSIS_URL = "http://localhost:8000/analyze";

    private List<JobMatchResultDTO> callAiResumeAnalysis(String resumeText, List<GoogleJob> allJobs) {
        RestTemplate restTemplate = new RestTemplate();
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        // Build job list for AI service
        List<Map<String, Object>> jobs = new ArrayList<>();
        for (GoogleJob job : allJobs) {
            Map<String, Object> jobMap = new HashMap<>();
            jobMap.put("id", job.getId());
            jobMap.put("jobId", job.getJobId());
            jobMap.put("title", job.getTitle());
            jobMap.put("companyName", job.getCompanyName());
            jobMap.put("location", job.getLocation());
            jobMap.put("qualifications", job.getQualifications());
            jobMap.put("description", job.getDescription());
            jobMap.put("responsibilities", job.getResponsibilities() != null ? job.getResponsibilities() : new ArrayList<>());
            jobMap.put("benefits", job.getBenefits() != null ? job.getBenefits() : new ArrayList<>());
            jobMap.put("salary", job.getSalary());
            jobMap.put("scheduleType", job.getScheduleType());
            jobMap.put("shareLink", job.getShareLink());
            jobMap.put("postedAt", job.getPostedAt());
            jobMap.put("applyLinks", job.getApplyLinks());
            jobMap.put("createdDateTime", job.getCreatedDateTime() != null ? job.getCreatedDateTime().format(isoFormatter) : null);
            jobMap.put("lastUpdatedDateTime", job.getLastUpdatedDateTime() != null ? job.getLastUpdatedDateTime().format(isoFormatter) : null);
            jobs.add(jobMap);
        }
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("resume_text", resumeText);
        requestBody.put("jobs", jobs);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(AI_ANALYSIS_URL, request, Map.class);
        log.info("Python AI response: {}", response.getBody());
        List<Map<String, Object>> topMatches = (List<Map<String, Object>>) response.getBody().get("top_matches");
        List<JobMatchResultDTO> result = new ArrayList<>();
        for (Map<String, Object> match : topMatches) {
            JobMatchResultDTO dto = new JobMatchResultDTO();
            // Set googleJobId from 'id' or 'jobId' as UUID
            String idStr = (String) match.get("id");
            if (idStr != null) {
                dto.setGoogleJobId(UUID.fromString(idStr));
            } else if (match.get("job_id") != null) {
                dto.setGoogleJobId(UUID.fromString((String) match.get("job_id")));
            }
            dto.setJobTitle((String) match.get("job_title"));
            dto.setCompanyName((String) match.get("company"));
            dto.setMatchPercentage(new java.math.BigDecimal(match.get("match_percentage").toString()));
            dto.setMatchedSkills(objectMapper.convertValue(match.get("matched_skills"), new TypeReference<List<String>>(){}));
            dto.setMissingSkills(objectMapper.convertValue(match.get("missing_skills"), new TypeReference<List<String>>(){}));
            dto.setLocation((String) match.get("location"));
            dto.setJobDescription((String) match.get("description"));
            dto.setQualifications((String) match.get("qualifications"));
            dto.setResponsibilities(objectMapper.convertValue(match.get("responsibilities"), new TypeReference<List<String>>(){}));
            dto.setBenefits(objectMapper.convertValue(match.get("benefits"), new TypeReference<List<String>>(){}));
            dto.setApplyLink((String) match.get("applyLink"));
            dto.setSalary((String) match.get("salary"));
            dto.setScheduleType((String) match.get("scheduleType"));
            dto.setCategoryScores(objectMapper.convertValue(match.get("categoryScores"), new TypeReference<Map<String, BigDecimal>>(){}));
            Object aiSuggestionsObj = match.get("ai_suggestions");
if (aiSuggestionsObj != null) {
    try {
        dto.setAiSuggestions(objectMapper.writeValueAsString(aiSuggestionsObj));
    } catch (Exception e) {
        dto.setAiSuggestions(null);
    }
} else {
    dto.setAiSuggestions(null);
}
            result.add(dto);
        }
        return result;
    }

    /**
     * Analyze resume against all Google Jobs and return comprehensive match results
     */
    public ResumeAnalysisDTO analyzeResumeAgainstAllJobs(MultipartFile file, String jobSeekerId) throws IOException {
        // Validate job seeker
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
            .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + jobSeekerId));

        // Extract text from resume
        File tempFile = saveFileTemporarily(file);
        String resumeText = extractTextFromFile(tempFile);
        
        // Save the resume file to disk only (do not save JobResume entity)
        String originalName = file.getOriginalFilename();
        String safeFirstName = jobSeeker.getFirstName().replaceAll("[^a-zA-Z0-9]", "_");
        String safeLastName = jobSeeker.getLastName().replaceAll("[^a-zA-Z0-9]", "_");
        String safeTitle = (jobSeeker.getPreferredJobTypes() != null && !jobSeeker.getPreferredJobTypes().isEmpty()) ? jobSeeker.getPreferredJobTypes().replaceAll("[^a-zA-Z0-9]", "_") : null;
        String filename = safeFirstName + "_" + safeLastName;
        if (safeTitle != null) {
            filename += "_" + safeTitle;
        }
        filename += "_" + originalName;
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(filename);
        // Check and delete existing file if present
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.copy(file.getInputStream(), filePath);
        String generatedResumeId = UUID.randomUUID().toString();
        LocalDateTime uploadedAt = LocalDateTime.now();
        
        // Extract skills from resume
        Set<String> resumeSkills = extractSkillsFromText(resumeText);
        // Extract skills by category for the DTO
        Map<String, Set<String>> skillsByCategory = extractSkillsByCategory(resumeText);
        List<String> extractedSkillsList = new ArrayList<>(resumeSkills);
        Map<String, List<String>> skillsByCategoryList = skillsByCategory.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ArrayList<>(entry.getValue())
            ));

        // Get all jobs
        List<GoogleJob> allJobs = googleJobRepository.findAll();
        // Call AI service for smart analysis
        List<JobMatchResultDTO> aiMatches = callAiResumeAnalysis(resumeText, allJobs);
        // Find the highest match percentage from aiMatches
        // (No longer needed, removed to fix unused variable warning)

        // Save a JobResume for each GoogleJob with its match percentage
        for (JobMatchResultDTO match : aiMatches) {
            // String aiSuggestionsJson = match.getAiSuggestions();
            String aiSuggestionsJson = String.join(", ", match.getAiSuggestions());
            JobResume jobResume = JobResume.builder()
                .id(UUID.randomUUID().toString())
                .googlejobId(match.getGoogleJobId() != null ? match.getGoogleJobId().toString() : null)
                .resumeFile(filename)
                .resumeText(resumeText)
                .uploadedAt(uploadedAt)
                .jobSeeker(jobSeeker)
                .matchPercentage(match.getMatchPercentage())
                .aiSuggestions(aiSuggestionsJson)
                .build();
            jobResumeRepository.save(jobResume);
        }

        // Calculate average match percentage
        BigDecimal averageMatchPercentage = aiMatches.isEmpty() ? BigDecimal.ZERO :
            aiMatches.stream().map(JobMatchResultDTO::getMatchPercentage).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(aiMatches.size()), 2, RoundingMode.HALF_UP);

        return ResumeAnalysisDTO.builder()
            .resumeId(generatedResumeId)
            .resumeFile(filename)
            .resumeText(resumeText)
            .uploadedAt(uploadedAt)
            .jobSeekerId(jobSeekerId)
            .jobSeekerName(jobSeeker.getFirstName() + " " + jobSeeker.getLastName())
            .extractedSkills(extractedSkillsList)
            .skillsByCategory(skillsByCategoryList)
            .jobMatches(aiMatches)
            .averageMatchPercentage(averageMatchPercentage)
            .totalJobsAnalyzed(aiMatches.size())
            .highMatchJobs(0)
            .mediumMatchJobs(0)
            .lowMatchJobs(0)
            .build();
    }


    

    public List<JobMatchResultDTO> getTopMatchingJobs(String resumeText, int limit, JobSeeker jobSeeker) {
        // Extract skills from resume text
        // TODO: Integrate AI analysis for job matching
        return new ArrayList<>();
    }

    /**
     * Get match percentages for each job for a specific resume (use stored matchPercentage)
     */
    public List<JobMatchResultDTO> getMatchPercentagesForResume(String resumeId) {
        JobResume resume = jobResumeRepository.findById(resumeId)
            .orElseThrow(() -> new EntityNotFoundException("Resume not found with id: " + resumeId));
        GoogleJob job = googleJobRepository.findById(resume.getGooglejobId()).orElse(null);
        if (job == null) return new ArrayList<>();
        JobMatchResultDTO dto = JobMatchResultDTO.builder()
            .googleJobId(UUID.fromString(resume.getGooglejobId()))
            .jobTitle(job.getTitle())
            .companyName(job.getCompanyName())
            .location(job.getLocation())
            .matchPercentage(resume.getMatchPercentage())
            .jobDescription(job.getDescription())
            .qualifications(job.getQualifications())
            .responsibilities(job.getResponsibilities())
            .benefits(job.getBenefits())
            .applyLink(job.getApplyLinks())
            .salary(job.getSalary())
            .scheduleType(job.getScheduleType())
            .jobSeekerId(resume.getJobSeeker() != null ? resume.getJobSeeker().getId() : null)
            .jobSeekerName(resume.getJobSeeker() != null ? resume.getJobSeeker().getFirstName() + " " + resume.getJobSeeker().getLastName() : null)
            .build();
        return List.of(dto);
    }


//     public ResumeAnalysisDTO paginatedResumeAnalysis(
//     MultipartFile file,
//     String jobSeekerId,
//     int page,
//     int size
// ) throws IOException {
//     // Validate job seeker
//     JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
//         .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + jobSeekerId));

//     // Extract text from resume
//     File tempFile = saveFileTemporarily(file);
//     String resumeText = extractTextFromFile(tempFile);

//     // Save the resume file to disk only (do not save JobResume entity)
//     String originalName = file.getOriginalFilename();
//     String safeFirstName = jobSeeker.getFirstName().replaceAll("[^a-zA-Z0-9]", "_");
//     String safeLastName = jobSeeker.getLastName().replaceAll("[^a-zA-Z0-9]", "_");
//     String safeTitle = (jobSeeker.getPreferredJobTypes() != null && !jobSeeker.getPreferredJobTypes().isEmpty()) ? jobSeeker.getPreferredJobTypes().replaceAll("[^a-zA-Z0-9]", "_") : null;
//     String filename = safeFirstName + "_" + safeLastName;
//     if (safeTitle != null) {
//         filename += "_" + safeTitle;
//     }
//     filename += "_" + originalName;
//     Path uploadPath = Paths.get(UPLOAD_DIR);
//     Files.createDirectories(uploadPath);
//     Path filePath = uploadPath.resolve(filename);
//     // Check and delete existing file if present
//     if (Files.exists(filePath)) {
//         Files.delete(filePath);
//     }
//     Files.copy(file.getInputStream(), filePath);
//     String generatedResumeId = UUID.randomUUID().toString();
//     LocalDateTime uploadedAt = LocalDateTime.now();

//     // Extract skills from resume
//     Set<String> resumeSkills = extractSkillsFromText(resumeText);
//     // Extract skills by category for the DTO
//     Map<String, Set<String>> skillsByCategory = extractSkillsByCategory(resumeText);
//     List<String> extractedSkillsList = new ArrayList<>(resumeSkills);
//     Map<String, List<String>> skillsByCategoryList = skillsByCategory.entrySet().stream()
//         .collect(Collectors.toMap(
//             Map.Entry::getKey,
//             entry -> new ArrayList<>(entry.getValue())
//         ));

//     // Get all jobs and paginate
//     List<GoogleJob> allJobs = googleJobRepository.findAll();
//     int fromIndex = page * size;
//     int toIndex                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                = Math.min(fromIndex + size, allJobs.size());
//     List<GoogleJob> paginatedJobs = allJobs.subList(fromIndex, toIndex);
//             List<JobMatchResultDTO> aiMatches = callAiResumeAnalysis(resumeText, allJobs);

//     // Call AI service for smart analysis (only for paginated jobs)
// // Call AI service for smart analysis (only for paginated jobs)
// RestTemplate restTemplate = new RestTemplate();
// String url = "http://localhost:8000/analyze";
// HttpHeaders headers = new HttpHeaders();
// headers.setContentType(MediaType.APPLICATION_JSON);
// Map<String, Object> payload = new HashMap<>();
// payload.put("jobs", paginatedJobs.stream().map(job -> {
//     Map<String, Object> jobMap = new HashMap<>();
//     jobMap.put("id", job.getId());
//     jobMap.put("jobId", job.getJobId());
//     jobMap.put("title", job.getTitle());
//     jobMap.put("companyName", job.getCompanyName());
//     jobMap.put("description", job.getDescription());
//     jobMap.put("qualifications", job.getQualifications());
//     jobMap.put("responsibilities", job.getResponsibilities());
//     // jobMap.put("skills", job.getSkills()); // Removed: GoogleJob has no getSkills()
//     return jobMap;
// }).collect(Collectors.toList()));
// payload.put("resume_text", resumeText);
// payload.put("job_seeker_id", jobSeekerId);
// HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
// ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
// String aiResponseBody = response.getBody();
// List<JobMatchResultDTO> jobMatches = new ArrayList<>();
// try {
//     com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(aiResponseBody);
//     com.fasterxml.jackson.databind.JsonNode matchesNode = root.get("top_matches");
//     if (matchesNode != null && matchesNode.isArray()) {
//         jobMatches = objectMapper.readValue(matchesNode.toString(), new com.fasterxml.jackson.core.type.TypeReference<List<JobMatchResultDTO>>(){});
//     }
// } catch (Exception e) {
//     log.error("Failed to parse AI job matches response", e);
// }

// // Calculate statistics
// BigDecimal averageMatchPercentage = BigDecimal.ZERO;
// int totalJobsAnalyzed = jobMatches.size();
// int highMatchJobs = 0, mediumMatchJobs = 0, lowMatchJobs = 0;
// if (totalJobsAnalyzed > 0) {
//     BigDecimal sum = BigDecimal.ZERO;
//     for (JobMatchResultDTO match : jobMatches) {
//         if (match.getMatchPercentage() != null) {
//             sum = sum.add(match.getMatchPercentage());
//             if (match.getMatchPercentage().compareTo(new BigDecimal("70")) >= 0) highMatchJobs++;
//             else if (match.getMatchPercentage().compareTo(new BigDecimal("40")) >= 0) mediumMatchJobs++;
//             else lowMatchJobs++;
//         }
//     }
//     averageMatchPercentage = sum.divide(BigDecimal.valueOf(totalJobsAnalyzed), 2, RoundingMode.HALF_UP);
// }

// // Pagination info
// int totalPages = (int) Math.ceil((double) allJobs.size() / size);
// boolean hasNextPage = (page + 1) < totalPages;
// boolean hasPreviousPage = page > 0;

// return ResumeAnalysisDTO.builder()
//     .resumeId(generatedResumeId)
//     .resumeFile(filename)
//     .resumeText(resumeText)
//     .uploadedAt(uploadedAt)
//     .jobSeekerId(jobSeekerId)
//     .jobSeekerName(jobSeeker.getFirstName() + " " + jobSeeker.getLastName())
//     .extractedSkills(extractedSkillsList)
//     .skillsByCategory(skillsByCategoryList)
//     .jobMatches(jobMatches)
//     .averageMatchPercentage(averageMatchPercentage)
//     .totalJobsAnalyzed(totalJobsAnalyzed)
//     .highMatchJobs(highMatchJobs)
//     .mediumMatchJobs(mediumMatchJobs)
//     .lowMatchJobs(lowMatchJobs)
//     .currentPage(page)
//     .totalPages(totalPages)
//     .pageSize(size)
//     .hasNextPage(hasNextPage)
//     .hasPreviousPage(hasPreviousPage)
//     .build();
// }


public ResumeAnalysisDTO paginatedResumeAnalysis(MultipartFile file, String jobSeekerId, int page, int size) throws IOException {
    // Validate job seeker
    JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
        .orElseThrow(() -> new EntityNotFoundException("Job seeker not found with id: " + jobSeekerId));

    // Extract text from resume
    File tempFile = saveFileTemporarily(file);
    String resumeText = extractTextFromFile(tempFile);
    
    // Save the resume file to disk
    String originalName = file.getOriginalFilename();
    String safeFirstName = jobSeeker.getFirstName().replaceAll("[^a-zA-Z0-9]", "_");
    String safeLastName = jobSeeker.getLastName().replaceAll("[^a-zA-Z0-9]", "_");
    String safeTitle = (jobSeeker.getPreferredJobTypes() != null && !jobSeeker.getPreferredJobTypes().isEmpty()) ? jobSeeker.getPreferredJobTypes().replaceAll("[^a-zA-Z0-9]", "_") : null;
    String filename = safeFirstName + "_" + safeLastName;
    if (safeTitle != null) {
        filename += "_" + safeTitle;
    }
    filename += "_" + originalName;
    Path uploadPath = Paths.get(UPLOAD_DIR);
    Files.createDirectories(uploadPath);
    Path filePath = uploadPath.resolve(filename);
    // Check and delete existing file if present
    if (Files.exists(filePath)) {
        Files.delete(filePath);
    }
    Files.copy(file.getInputStream(), filePath);
    String generatedResumeId = UUID.randomUUID().toString();
    LocalDateTime uploadedAt = LocalDateTime.now();
    
    // Extract skills from resume
    Set<String> resumeSkills = extractSkillsFromText(resumeText);
    Map<String, Set<String>> skillsByCategory = extractSkillsByCategory(resumeText);
    List<String> extractedSkillsList = new ArrayList<>(resumeSkills);
    Map<String, List<String>> skillsByCategoryList = skillsByCategory.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> new ArrayList<>(entry.getValue())
        ));

    // Pagination for the jobs
    Pageable pageable = PageRequest.of(page, size);  // Create pageable object with page number and size
    Page<GoogleJob> jobPage = googleJobRepository.findAll(pageable);  // Fetch paginated jobs from the repository
    List<GoogleJob> jobsInPage = jobPage.getContent();  // Get the content (list of jobs) for the current page

    // Call AI service for analysis based on paginated jobs
    List<JobMatchResultDTO> aiMatches = callAiResumeAnalysis(resumeText, jobsInPage);

    // Save a JobResume for each GoogleJob with its match percentage
    for (JobMatchResultDTO match : aiMatches) {
        String aiSuggestionsJson = match.getAiSuggestions();
        JobResume jobResume = JobResume.builder()
            .id(UUID.randomUUID().toString())
            .googlejobId(match.getGoogleJobId() != null ? match.getGoogleJobId().toString() : null)
            .resumeFile(filename)
            .resumeText(resumeText)
            .uploadedAt(uploadedAt)
            .jobSeeker(jobSeeker)
            .matchPercentage(match.getMatchPercentage())
            .aiSuggestions(aiSuggestionsJson)
            .build();
        jobResumeRepository.save(jobResume);
    }

    // Calculate average match percentage
    BigDecimal averageMatchPercentage = aiMatches.isEmpty() ? BigDecimal.ZERO :
        aiMatches.stream().map(JobMatchResultDTO::getMatchPercentage).reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(aiMatches.size()), 2, RoundingMode.HALF_UP);

    // Pagination metadata
    long totalJobs = jobPage.getTotalElements();  // Total number of jobs available
    int totalPages = jobPage.getTotalPages();  // Total number of pages
    boolean hasNextPage = jobPage.hasNext();  // Check if there's a next page
    boolean hasPreviousPage = jobPage.hasPrevious();  // Check if there's a previous page

    return ResumeAnalysisDTO.builder()
        .resumeId(generatedResumeId)
        .resumeFile(filename)
        .resumeText(resumeText)
        .uploadedAt(uploadedAt)
        .jobSeekerId(jobSeekerId)
        .jobSeekerName(jobSeeker.getFirstName() + " " + jobSeeker.getLastName())
        .extractedSkills(extractedSkillsList)
        .skillsByCategory(skillsByCategoryList)
        .jobMatches(aiMatches)
        .averageMatchPercentage(averageMatchPercentage)
        .totalJobsAnalyzed(aiMatches.size())
        .highMatchJobs(0)
        .mediumMatchJobs(0)
        .lowMatchJobs(0)
        .currentPage(page)  // Current page number
        .totalPages(totalPages)  // Total number of pages
        .pageSize(size)  // Size of each page
        .hasNextPage(hasNextPage)  // Indicates if there's a next page
        .hasPreviousPage(hasPreviousPage)  // Indicates if there's a previous page
        .build();
}


    // public ResumeAnalysisDTO getResumeAnalysisByJobSeeker(String jobSeekerId, int page, int size) {
    // Fetch paginated jobs for the job seeker
//     Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt"));
//     Page<JobResume> jobResumePage = jobResumeRepository.findByJobSeeker_Id(jobSeekerId, pageable);
//     List<JobResume> jobResumes = jobResumePage.getContent();

//     // Prepare request payload for Python backend
//     List<Map<String, Object>> jobsForAnalysis = new ArrayList<>();
//     for (JobResume jr : jobResumes) {
//         Map<String, Object> jobMap = new HashMap<>();
//         jobMap.put("googleJobId", jr.getGooglejobId());
//         jobMap.put("resumeText", jr.getResumeText());
//         jobMap.put("jobSeekerId", jobSeekerId);
//         jobsForAnalysis.add(jobMap);
//     }
//     Map<String, Object> payload = new HashMap<>();
//     payload.put("jobs", jobsForAnalysis);
//     payload.put("jobSeekerId", jobSeekerId);

//     // Call Python FastAPI backend
//     String url = "http://localhost:8000/analyze";
//     HttpHeaders headers = new HttpHeaders();
//     headers.setContentType(MediaType.APPLICATION_JSON);
//     RestTemplate restTemplate = new RestTemplate();
//     HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
//     ResponseEntity<JobMatchResultDTO[]> response = restTemplate.postForEntity(url, entity, JobMatchResultDTO[].class);
//     JobMatchResultDTO[] result = response.getBody();
//     return result != null ? Arrays.asList(result) : Collections.emptyList();
// }

    /**
     * Get match percentages for all resumes of a job seeker (use stored matchPercentage)
     */
    public List<JobMatchResultDTO> getMatchPercentagesForJobSeeker(String jobSeekerId) {
        List<JobResume> resumes = jobResumeRepository.findByJobSeeker_Id(jobSeekerId);
        List<JobMatchResultDTO> results = new ArrayList<>();
        for (JobResume resume : resumes) {
            GoogleJob job = googleJobRepository.findById(resume.getGooglejobId()).orElse(null);
            if (job != null) {
                JobMatchResultDTO dto = JobMatchResultDTO.builder()
                    .googleJobId(UUID.fromString(resume.getGooglejobId()))
                    .jobTitle(job.getTitle())
                    .companyName(job.getCompanyName())
                    .location(job.getLocation())
                    .matchPercentage(resume.getMatchPercentage())
                    .jobDescription(job.getDescription())
                    .qualifications(job.getQualifications())
                    .responsibilities(job.getResponsibilities())
                    .benefits(job.getBenefits())
                    .applyLink(job.getApplyLinks())
                    .salary(job.getSalary())
                    .scheduleType(job.getScheduleType())
                    .jobSeekerId(resume.getJobSeeker() != null ? resume.getJobSeeker().getId() : null)
                    .jobSeekerName(resume.getJobSeeker() != null ? resume.getJobSeeker().getFirstName() + " " + resume.getJobSeeker().getLastName() : null)
                    .build();
                results.add(dto);
            }
        }
        results.sort((a, b) -> b.getMatchPercentage().compareTo(a.getMatchPercentage()));
        return results;
    }

    /**
     * Get match percentages for all resumes for a specific job (use stored matchPercentage)
     */
    public List<JobMatchResultDTO> getMatchPercentagesForJob(String googleJobId) {
        List<JobResume> resumes = jobResumeRepository.findByGooglejobId(googleJobId);
        List<JobMatchResultDTO> results = new ArrayList<>();
        GoogleJob job = googleJobRepository.findById(googleJobId).orElse(null);
        if (job == null) return new ArrayList<>();
        for (JobResume resume : resumes) {
            JobMatchResultDTO dto = JobMatchResultDTO.builder()
                .googleJobId(UUID.fromString(googleJobId))
                .jobTitle(job.getTitle())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .matchPercentage(resume.getMatchPercentage())
                .jobDescription(job.getDescription())
                .qualifications(job.getQualifications())
                .responsibilities(job.getResponsibilities())
                .benefits(job.getBenefits())
                .applyLink(job.getApplyLinks())
                .salary(job.getSalary())
                .scheduleType(job.getScheduleType())
                .jobSeekerId(resume.getJobSeeker() != null ? resume.getJobSeeker().getId() : null)
                .jobSeekerName(resume.getJobSeeker() != null ? resume.getJobSeeker().getFirstName() + " " + resume.getJobSeeker().getLastName() : null)
                .build();
            results.add(dto);
        }
        results.sort((a, b) -> b.getMatchPercentage().compareTo(a.getMatchPercentage()));
        return results;
    }

    // Download improved resume as DOCX from FastAPI
    private void downloadDocxFromFastApi(String resumeText, String outputFilePath) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            log.warn("No resume text provided for DOCX download.");
            return;
        }
        try {
            // Ensure parent directory exists
            java.nio.file.Path filePath = java.nio.file.Path.of(outputFilePath);
            java.nio.file.Files.createDirectories(filePath.getParent());

            String safeText = resumeText.replace("\"", "\\\"").replace("\n", "\\n");
            String json = "{\"resume_text\":\"" + safeText + "\"}";

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8000/download_resume"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                .build();

            java.net.http.HttpResponse<byte[]> response = client.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                log.error("Failed to download DOCX from FastAPI. Status: {}", response.statusCode());
                return;
            }

            java.nio.file.Files.write(filePath, response.body());
            log.info("Resume DOCX downloaded to {}", filePath.toAbsolutePath());
        } catch (Exception e) {
            log.error("Exception while downloading DOCX from FastAPI: {}", e.getMessage(), e);
        }
    }

    // This endpoint now proxies requests to the Python backend for resume improvement and AI-based matching.
// All logic is handled in Python. This Java service only forwards requests and returns responses.
    public AutoImproveResponse autoImproveResume(AutoImproveRequest request) {
        // Proxy to Python backend
        RestTemplate restTemplate = new RestTemplate();
        String pythonUrl = "http://localhost:8000/improve";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AutoImproveRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<AutoImproveResponse> response = restTemplate.postForEntity(
            pythonUrl, entity, AutoImproveResponse.class
        );
        AutoImproveResponse aiResponse = response.getBody();

        // Update JobResume in DB
        if (aiResponse != null && aiResponse.getMatchPercentage() != null && aiResponse.getMatchPercentage().compareTo(new java.math.BigDecimal("100")) == 0) {
            aiResponse.setCanDownload(true);
            // Download DOCX from FastAPI
            try {
                String outputFilePath = UPLOAD_DIR + "/updated_resume_" + request.getJobSeekerId() + "_" + request.getGoogleJobId() + ".docx";
                downloadDocxFromFastApi(aiResponse.getResumeText(), outputFilePath);
                // Optionally: store the path in DB or return as part of response
            } catch (Exception e) {
                // Handle error, log, etc.
                log.error("Failed to download DOCX from FastAPI: {}", e.getMessage(), e);
            }
        }
        if (request.getJobSeekerId() != null && request.getGoogleJobId() != null && aiResponse != null) {
            List<JobResume> jobResumes = jobResumeRepository.findByJobSeeker_IdAndGooglejobId(
                request.getJobSeekerId(), request.getGoogleJobId().toString()
            );
            if (jobResumes != null && !jobResumes.isEmpty()) {
                // Update the latest resume (by uploadedAt)
                jobResumes.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
                JobResume jobResume = jobResumes.get(0);
                jobResume.setResumeText(aiResponse.getResumeText());
                jobResume.setMatchPercentage(aiResponse.getMatchPercentage());
                jobResume.setUploadedAt(java.time.LocalDateTime.now()); // Ensure improved resume is latest
                try {
                    // Convert suggestions to JSON if needed
                    if (aiResponse.getSuggestions() != null) {
                        jobResume.setAiSuggestions(objectMapper.writeValueAsString(aiResponse.getSuggestions()));
                    }
                } catch (Exception e) {
                    jobResume.setAiSuggestions(null);
                }
                jobResumeRepository.save(jobResume);
            }
        }
        return aiResponse;
    }

    /**
     * Saves a MultipartFile to a temporary file and returns the File reference.
     */
    public File saveFileTemporarily(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String suffix = (originalFilename != null && originalFilename.contains(".")) ?
            originalFilename.substring(originalFilename.lastIndexOf('.')) : null;
        Path tempPath = java.nio.file.Files.createTempFile("resume-", suffix);
        try (java.io.InputStream in = file.getInputStream()) {
            java.nio.file.Files.copy(in, tempPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return tempPath.toFile();
    }

}

    
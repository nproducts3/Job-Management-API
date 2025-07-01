package com.ensar.jobs.service;

import com.ensar.jobs.dto.JobResumeDTO;
import com.ensar.jobs.entity.GoogleJob;
import com.ensar.jobs.entity.JobResume;
import com.ensar.jobs.repository.GoogleJobRepository;
import com.ensar.jobs.repository.JobResumeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobResumeService {
    private final GoogleJobRepository googleJobRepository;
    private final JobResumeRepository jobResumeRepository;
    private final ObjectMapper objectMapper;
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
    }

    public JobResumeDTO uploadAndProcessResume(MultipartFile file, UUID googleJobId) throws IOException {
        try {
            if (googleJobId == null) {
                throw new IllegalArgumentException("Invalid Google Job ID (googleJobId) format.");
            }

            // Delete existing resume(s) for this job
            List<JobResume> existingResumes = jobResumeRepository.findByGooglejobId(googleJobId);
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

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            log.info("File saved successfully: {}", filename);

            // Extract text from the resume file
            String resumeText = extractTextFromFile(file, filePath.toFile());
            log.info("Successfully extracted text from resume, length: {} characters", resumeText.length());

            // Fetch GoogleJob for job title and company name
            GoogleJob googleJob = googleJobRepository.findById(googleJobId.toString())
                .orElse(null);
            String jobTitle = googleJob != null ? googleJob.getTitle() : null;
            String companyName = googleJob != null ? googleJob.getCompanyName() : null;

            // Extract job skills from GoogleJob if available
            Set<String> jobSkills = googleJob != null ? extractJobSkills(googleJob) : new HashSet<>();
            Map<String, Set<String>> resumeSkillsByCategory = extractSkillsByCategory(resumeText);
            Set<String> resumeSkills = resumeSkillsByCategory.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
            SkillMatchResult matchResult = calculateDetailedSkillMatch(jobSkills, resumeSkills, resumeSkillsByCategory);

            JobResume jobResume = JobResume.builder()
                    .id(UUID.randomUUID().toString())
                    .googlejobId(googleJobId)
                    .resumeFile(filename)
                    .resumeText(resumeText)
                    .matchPercentage(matchResult.getMatchPercentage())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            jobResume = jobResumeRepository.save(jobResume);
            log.info("Successfully saved job resume with ID: {}", jobResume.getId());

            return JobResumeDTO.builder()
                    .id(jobResume.getId())
                    .googleJobId(googleJobId)
                    .resumeFile(filename)
                    .resumeText(resumeText)
                    .matchPercentage(matchResult.getMatchPercentage())
                    .uploadedAt(jobResume.getUploadedAt())
                    .jobTitle(jobTitle)
                    .companyName(companyName)
                    .matchedSkills(matchResult.getMatchedSkills() != null ? String.join(", ", matchResult.getMatchedSkills()) : null)
                    .missingSkills(matchResult.getMissingSkills() != null ? String.join(", ", matchResult.getMissingSkills()) : null)
                    .build();
        } catch (Exception e) {
            log.error("Error processing resume upload", e);
            throw e;
        }
    }

    private String extractTextFromFile(MultipartFile file, java.io.File savedFile) throws IOException {
        String contentType = file.getContentType();
        log.info("Processing file with content type: {}", contentType);
        
        if (contentType == null) {
            throw new IllegalArgumentException("Content type is null");
        }

        String extractedText;
        switch (contentType) {
            case "application/pdf":
                extractedText = extractTextFromPDF(savedFile);
                break;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                extractedText = extractTextFromDOCX(savedFile);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type: " + contentType);
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

    private Set<String> extractSkillsFromText(String text) {
        Set<String> foundSkills = new HashSet<>();
        String normalizedText = text.toLowerCase();
        
        log.info("Starting skill extraction from text");

        // First look for skills section
        Pattern skillSectionPattern = Pattern.compile(
            "(?i)(skills|expertise|competencies|qualifications|proficiencies)[:\\s]*([^\\n]*(?:\\n(?!\\n)[^\\n]*)*)"
        );
        
        Matcher sectionMatcher = skillSectionPattern.matcher(normalizedText);
        while (sectionMatcher.find()) {
            String skillsSection = sectionMatcher.group(2);
            log.info("Found skills section: {}", skillsSection);
            
            // Split by common delimiters
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

        log.info("Found skills: {}", foundSkills);
        return foundSkills;
    }

    private void processSkill(String text, Set<String> foundSkills) {
        String normalizedText = text.toLowerCase();
        
        // Check for direct matches in our skill variations
        for (Map.Entry<String, Set<String>> entry : TECHNICAL_SKILL_VARIATIONS.entrySet()) {
            if (entry.getValue().stream().anyMatch(variation -> 
                normalizedText.contains(variation) || 
                variation.contains(normalizedText))) {
                foundSkills.add(entry.getKey());
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

    private Map<String, Set<String>> extractSkillsByCategory(String text) {
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

    @lombok.Value
    private static class SkillMatchResult {
        BigDecimal matchPercentage;
        Set<String> matchedSkills;
        Set<String> missingSkills;
        Map<String, BigDecimal> categoryScores;
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
        List<JobResume> resumes = jobResumeRepository.findByGooglejobId(googleJobId);
        GoogleJob googleJob = googleJobRepository.findById(googleJobId.toString()).orElse(null);
        String jobTitle = googleJob != null ? googleJob.getTitle() : null;
        String companyName = googleJob != null ? googleJob.getCompanyName() : null;
        Set<String> jobSkills = googleJob != null ? extractJobSkills(googleJob) : new HashSet<>();
        return resumes.stream().map(resume -> {
            JobResumeDTO dto = new JobResumeDTO();
            dto.setId(resume.getId());
            dto.setGoogleJobId(googleJobId);
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
        List<JobResume> existingResumes = jobResumeRepository.findByGooglejobId(googleJobId);
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
}
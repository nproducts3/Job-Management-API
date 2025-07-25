package com.ensar.jobs.controller;

import com.ensar.jobs.dto.JobSeekerSkillDTO;
import com.ensar.jobs.service.JobSeekerSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/job-seeker-skills")
@Tag(name = "Job Seeker Skills", description = "CRUD operations for job seeker skills")
public class JobSeekerSkillController {
    @Autowired
    private JobSeekerSkillService skillService;

    @PostMapping
    @Operation(summary = "Create a new job seeker skill")
    public ResponseEntity<JobSeekerSkillDTO> create(@RequestBody JobSeekerSkillDTO dto) {
        return ResponseEntity.ok(skillService.createJobSeekerSkill(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job seeker skill")
    public ResponseEntity<JobSeekerSkillDTO> update(@PathVariable String id, @RequestBody JobSeekerSkillDTO dto) {
        return ResponseEntity.ok(skillService.updateJobSeekerSkill(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job seeker skill by ID")
    public ResponseEntity<JobSeekerSkillDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(skillService.getJobSeekerSkillById(id));
    }

    @GetMapping("/by-jobseeker")
    @Operation(summary = "Get all job seeker skills by jobSeekerId")
    public ResponseEntity<List<JobSeekerSkillDTO>> getAllByJobSeekerSkillId(@RequestParam String jobSeekerId) {
        return ResponseEntity.ok(skillService.getAllByJobSeekerSkillId(jobSeekerId));
    }

    @GetMapping
    @Operation(summary = "Get all job seeker skills")
    public ResponseEntity<List<JobSeekerSkillDTO>> getAll() {
        return ResponseEntity.ok(skillService.getAllJobSeekerSkills());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job seeker skill")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        skillService.deleteJobSeekerSkill(id);
        return ResponseEntity.noContent().build();
    }
} 
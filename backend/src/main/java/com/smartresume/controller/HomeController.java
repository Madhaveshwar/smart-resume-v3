package com.smartresume.controller;

import com.smartresume.model.Resume;
import com.smartresume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ResumeController {

    private final ResumeService resumeService;

    @Autowired
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/job-titles")
    public ResponseEntity<List<String>> getJobTitles() {
        return ResponseEntity.ok(resumeService.getJobTitles());
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processResumes(
            @RequestParam("pdf_docs") List<MultipartFile> files,
            @RequestParam(value = "job_title", defaultValue = "") String jobTitle,
            @RequestParam(value = "job_desc",  defaultValue = "") String jobDesc) {

        List<Resume> results = resumeService.processAndSave(files, jobTitle, jobDesc);

        List<Map<String, Object>> resumeList = new ArrayList<>();
        for (Resume r : results) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name",             r.getName());
            m.put("email",            r.getEmail());
            m.put("phone",            r.getPhone());
            m.put("experience",       r.getExperience());
            m.put("fileName",         r.getFileName());
            m.put("matchPercentage",  r.getMatchPercentage());
            m.put("score",            r.getScore());
            m.put("detectedSkills",   r.getSkillsList());
            m.put("matchedSkills",    r.getMatchedSkillsList());
            m.put("missingSkills",    r.getMissingSkillsList());
            m.put("suggestions",      r.getSuggestionsList());
            m.put("recommendedRoles", r.getRecommendedRolesList());
            m.put("suggestedJobs",    r.getSuggestedJobsList());
            if (r.getUploadedAt() != null) m.put("uploadedAt", r.getUploadedAt().toString());
            resumeList.add(m);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("jobTitle",     jobTitle);
        response.put("totalResumes", results.size());
        response.put("resumes",      resumeList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchResumes(
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        return ResponseEntity.ok(mapResumes(resumeService.searchByKeyword(keyword)));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        List<Resume> all = resumeService.getAllResumes();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalStored", all.size());
        response.put("resumes",     mapResumes(all));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clear")
    public ResponseEntity<Map<String, String>> clearAll() {
        resumeService.clearAll();
        Map<String, String> resp = new HashMap<>();
        resp.put("status",  "success");
        resp.put("message", "All resumes cleared.");
        return ResponseEntity.ok(resp);
    }

    private List<Map<String, Object>> mapResumes(List<Resume> resumes) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Resume r : resumes) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",               r.getId());
            m.put("name",             r.getName());
            m.put("email",            r.getEmail());
            m.put("phone",            r.getPhone());
            m.put("experience",       r.getExperience());
            m.put("fileName",         r.getFileName());
            m.put("matchPercentage",  r.getMatchPercentage());
            m.put("score",            r.getScore());
            m.put("detectedSkills",   r.getSkillsList());
            m.put("matchedSkills",    r.getMatchedSkillsList());
            m.put("missingSkills",    r.getMissingSkillsList());
            m.put("suggestions",      r.getSuggestionsList());
            m.put("recommendedRoles", r.getRecommendedRolesList());
            m.put("suggestedJobs",    r.getSuggestedJobsList());
            if (r.getUploadedAt() != null) m.put("uploadedAt", r.getUploadedAt().toString());
            list.add(m);
        }
        return list;
    }
}

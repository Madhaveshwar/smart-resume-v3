package com.smartresume.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // allow frontend endpoints including Antigravity
public class ResumeController {

    @GetMapping("/job-titles")
    public List<String> getJobTitles() {
        return Arrays.asList(
                "Software Engineer",
                "Data Scientist",
                "Web Developer",
                "AI Engineer",
                "Backend Developer",
                "Frontend Developer");
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobTitle", required = false, defaultValue = "") String jobTitle,
            @RequestParam(value = "jobDescription", required = false, defaultValue = "") String jobDescription) {

        // 1. Temporary simulated extracted text
        String resumeText = "java spring sql react";

        // 2. Define expected skills
        List<String> skills = Arrays.asList(
                "java", "python", "sql", "spring", "react", "html", "css");

        // 3. Compare extracted text against skills
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : skills) {
            if (resumeText.contains(skill.toLowerCase())) {
                matched.add(skill);
            } else {
                missing.add(skill);
            }
        }

        // 4. Calculate score percentage
        int score = (matched.size() * 100) / skills.size();

        // 5. Build proper response for the UI card
        Map<String, Object> result = new HashMap<>();
        String fileName = file.getOriginalFilename();
        result.put("name", fileName != null && !fileName.isEmpty() ? fileName : "Candidate");
        result.put("score", score);
        result.put("matchedSkills", matched);
        result.put("missingSkills", missing);

        // Personalised AI Improvement Suggestions
        List<String> suggestions = new ArrayList<>();
        for (String m : missing) {
            suggestions.add("Add " + m.toUpperCase() + " projects or keywords to improve your match.");
        }
        result.put("suggestions", suggestions);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "File analyzed successfully ✅");
        response.put("jobTitle", jobTitle != null && !jobTitle.isEmpty() ? jobTitle : "General Application");
        response.put("totalResumes", 1);
        response.put("resumes", Collections.singletonList(result));

        return response;
    }
}
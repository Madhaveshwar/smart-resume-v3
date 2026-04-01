package com.smartresume.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // allow frontend
public class ResumeController {

    @GetMapping("/job-titles")
    public List<String> getJobTitles() {
        return Arrays.asList(
                "Software Engineer",
                "Data Scientist",
                "Web Developer",
                "AI Engineer",
                "Backend Developer",
                "Frontend Developer"
        );
    }

    @PostMapping("/upload")
    public Map<String, String> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam(value = "jobDescription", required = false) String jobDescription
    ) {

        Map<String, String> response = new HashMap<>();

        response.put("message", "File uploaded successfully ✅");
        response.put("jobTitle", jobTitle);

        return response;
    }
}
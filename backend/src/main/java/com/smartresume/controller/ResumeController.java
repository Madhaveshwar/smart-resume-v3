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

    // ✅ TEST API (FIXED)
    @GetMapping("/")
    public String testApi() {
        return "API is working ✅";
    }

    // ✅ DEBUG (VERY IMPORTANT)
    @GetMapping("/debug")
    public String debug() {
        return "DEBUG OK 🚀";
    }

    // ✅ JOB TITLES
    @GetMapping("/job-titles")
    public List<String> getJobTitles() {
        return List.of(
                "Software Engineer",
                "Data Scientist",
                "Web Developer",
                "AI Engineer",
                "DevOps Engineer"
        );
    }

    // ✅ PROCESS RESUMES
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processResumes(
            @RequestParam("pdf_docs") List<MultipartFile> files,
            @RequestParam(value = "job_title", defaultValue = "") String jobTitle,
            @RequestParam(value = "job_desc", defaultValue = "") String jobDesc) {

        List<Resume> results = resumeService.processAndSave(files, jobTitle, jobDesc);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("jobTitle", jobTitle);
        response.put("totalResumes", results.size());
        response.put("resumes", results);

        return ResponseEntity.ok(response);
    }
}
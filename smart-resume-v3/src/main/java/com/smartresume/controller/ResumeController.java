package com.smartresume.controller;

import com.smartresume.model.Resume;
import com.smartresume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
public class ResumeController {

    private final ResumeService resumeService;

    @Autowired
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("jobTitles", resumeService.getJobTitles());
        model.addAttribute("resumes",   Collections.emptyList());
        model.addAttribute("mode",      "upload");
        return "index";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("pdf_docs")            List<MultipartFile> files,
            @RequestParam(value = "job_title",   defaultValue = "") String jobTitle,
            @RequestParam(value = "job_desc",    defaultValue = "") String jobDesc,
            Model model) {

        List<Resume> results = resumeService.processAndSave(files, jobTitle, jobDesc);
        model.addAttribute("jobTitles",      resumeService.getJobTitles());
        model.addAttribute("resumes",        results);
        model.addAttribute("jobTitle",       jobTitle);
        model.addAttribute("mode",           "results");
        model.addAttribute("totalProcessed", results.size());
        return "index";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            Model model) {
        List<Resume> results = resumeService.searchByKeyword(keyword);
        model.addAttribute("jobTitles", resumeService.getJobTitles());
        model.addAttribute("resumes",   results);
        model.addAttribute("keyword",   keyword);
        model.addAttribute("mode",      "search");
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Resume> all = resumeService.getAllResumes();
        model.addAttribute("jobTitles",    resumeService.getJobTitles());
        model.addAttribute("resumes",      all);
        model.addAttribute("mode",         "dashboard");
        model.addAttribute("totalStored",  all.size());
        return "index";
    }

    @PostMapping("/clear")
    public String clearAll() {
        resumeService.clearAll();
        return "redirect:/dashboard";
    }

    // ── REST API ───────────────────────────────────────────────────

    @GetMapping("/api/job-titles")
    @ResponseBody
    public ResponseEntity<List<String>> getJobTitles() {
        return ResponseEntity.ok(resumeService.getJobTitles());
    }

    @PostMapping("/api/process")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processApi(
            @RequestParam("pdf_docs")          List<MultipartFile> files,
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
            m.put("matchPercentage",  r.getMatchPercentage());
            m.put("score",            r.getScore());
            m.put("detectedSkills",   r.getSkillsList());
            m.put("matchedSkills",    r.getMatchedSkillsList());
            m.put("missingSkills",    r.getMissingSkillsList());
            m.put("suggestions",      r.getSuggestionsList());
            m.put("recommendedRoles", r.getRecommendedRolesList());
            m.put("suggestedJobs",    r.getSuggestedJobsList());
            resumeList.add(m);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("jobTitle",     jobTitle);
        response.put("totalResumes", results.size());
        response.put("resumes",      resumeList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<Resume>> searchApi(
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        return ResponseEntity.ok(resumeService.searchByKeyword(keyword));
    }
}

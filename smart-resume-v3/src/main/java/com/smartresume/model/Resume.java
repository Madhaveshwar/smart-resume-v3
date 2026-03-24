package com.smartresume.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "matched_skills", columnDefinition = "TEXT")
    private String matchedSkills;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "suggestions", columnDefinition = "LONGTEXT")
    private String suggestions;

    @Column(name = "experience", length = 100)
    private String experience;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "raw_text", columnDefinition = "LONGTEXT")
    private String rawText;

    @Column(name = "score")
    private int score;

    @Column(name = "match_percentage")
    private int matchPercentage;

    @Column(name = "suggested_jobs", columnDefinition = "TEXT")
    private String suggestedJobs;

    @Column(name = "recommended_roles", columnDefinition = "TEXT")
    private String recommendedRoles;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(String matchedSkills) { this.matchedSkills = matchedSkills; }
    public String getMissingSkills() { return missingSkills; }
    public void setMissingSkills(String missingSkills) { this.missingSkills = missingSkills; }
    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }
    public String getSuggestedJobs() { return suggestedJobs; }
    public void setSuggestedJobs(String suggestedJobs) { this.suggestedJobs = suggestedJobs; }
    public String getRecommendedRoles() { return recommendedRoles; }
    public void setRecommendedRoles(String recommendedRoles) { this.recommendedRoles = recommendedRoles; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    // ── Convenience list helpers for Thymeleaf ──────────────────────
    private List<String> splitCsv(String val) {
        if (val == null || val.isBlank()) return Collections.emptyList();
        return Arrays.asList(val.split("\\|\\|"));
    }

    @Transient public List<String> getSkillsList()         { return splitCsv(skills); }
    @Transient public List<String> getMatchedSkillsList()  { return splitCsv(matchedSkills); }
    @Transient public List<String> getMissingSkillsList()  { return splitCsv(missingSkills); }
    @Transient public List<String> getSuggestionsList()    { return splitCsv(suggestions); }
    @Transient public List<String> getSuggestedJobsList()  { return splitCsv(suggestedJobs); }
    @Transient public List<String> getRecommendedRolesList() { return splitCsv(recommendedRoles); }
}

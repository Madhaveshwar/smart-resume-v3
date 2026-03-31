package com.smartresume.service;

import com.smartresume.model.Resume;
import com.smartresume.repository.ResumeRepository;
import com.smartresume.service.MatchingService.MatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final TikaParserService tikaParser;
    private final MatchingService   matchingService;
    private final ResumeRepository  resumeRepository;

    @Autowired
    public ResumeService(TikaParserService tikaParser,
                         MatchingService matchingService,
                         ResumeRepository resumeRepository) {
        this.tikaParser       = tikaParser;
        this.matchingService  = matchingService;
        this.resumeRepository = resumeRepository;
    }

    public List<Resume> processAndSave(List<MultipartFile> files,
                                       String jobTitle,
                                       String jobDescription) {
        List<Resume> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String text = tikaParser.extractText(file);
                if (text.isBlank()) continue;
                Resume resume = buildResume(file.getOriginalFilename(), text, jobTitle, jobDescription);
                resumeRepository.save(resume);
                results.add(resume);
            } catch (Exception ignored) {}
        }
        results.sort(Comparator.comparingInt(Resume::getMatchPercentage).reversed());
        return results;
    }

    public List<Resume> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank())
            return resumeRepository.findAllByOrderByMatchPercentageDesc();
        return resumeRepository.searchByKeyword(keyword.trim());
    }

    public List<Resume> getAllResumes() {
        return resumeRepository.findAllByOrderByMatchPercentageDesc();
    }

    public void clearAll() { resumeRepository.deleteAll(); }

    public List<String> getJobTitles() { return matchingService.getJobTitles(); }

    // ── Build resume with full ATS analysis ─────────────────────────
    private Resume buildResume(String fileName, String text,
                               String jobTitle, String jobDescription) {
        Resume r = new Resume();
        r.setFileName(fileName);
        r.setRawText(text);
        r.setName(extractName(text));
        r.setEmail(extractEmail(text));
        r.setPhone(extractPhone(text));
        r.setExperience(extractExperience(text));

        List<String> allSkills = matchingService.extractSkills(text);
        r.setSkills(String.join("||", allSkills));

        int pct = 0;

        if (jobTitle != null && !jobTitle.isBlank()) {
            MatchResult result = matchingService.matchForJobTitle(text, jobTitle);
            pct = result.getMatchPercentage();

            r.setMatchedSkills(String.join("||", result.getMatchedSkills()));
            r.setMissingSkills(String.join("||", result.getMissingSkills()));
            r.setSuggestions(String.join("||", result.getSuggestions()));
            r.setRecommendedRoles(String.join("||", result.getRecommendedRoles()));

            // Blend with cosine if JD also provided
            if (jobDescription != null && !jobDescription.isBlank()) {
                int cosine  = matchingService.cosineSimilarity(text, jobDescription);
                int keyword = matchingService.matchAgainstKeywords(text, jobDescription);
                pct = (int) (pct * 0.6 + keyword * 0.25 + cosine * 0.15);
            }
        } else if (jobDescription != null && !jobDescription.isBlank()) {
            int keyword = matchingService.matchAgainstKeywords(text, jobDescription);
            int cosine  = matchingService.cosineSimilarity(text, jobDescription);
            pct = (int) (keyword * 0.7 + cosine * 0.3);
        } else {
            pct = Math.min(100, allSkills.size() * 5);
        }

        List<String> allTokens = jobDescription != null && !jobDescription.isBlank()
                ? Arrays.asList(jobDescription.split("\\s+"))
                : allSkills;
        int rawScore = matchingService.calculateWeightedScore(text, allTokens);
        r.setScore(rawScore);
        r.setMatchPercentage(Math.min(100, pct));

        // Suggested jobs (low match or no job title given)
        if (pct < 50 || (jobTitle == null || jobTitle.isBlank())) {
            List<String> suggested = matchingService.suggestJobs(allSkills);
            r.setSuggestedJobs(String.join("||", suggested));
        }

        return r;
    }

    // ── Text extraction helpers ──────────────────────────────────────
    private String extractName(String text) {
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] words = line.split("\\s+");
            if (words.length >= 2 && words.length <= 5
                    && Character.isUpperCase(words[0].charAt(0))) {
                String lower = line.toLowerCase();
                if (!lower.contains("resume") && !lower.contains("curriculum")
                        && !lower.contains("profile") && !lower.contains("@")) {
                    return line;
                }
            }
        }
        return "Unknown";
    }

    private String extractEmail(String text) {
        Matcher m = Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}").matcher(text);
        return m.find() ? m.group() : "Not found";
    }

    private String extractPhone(String text) {
        Matcher m = Pattern.compile(
                "(\\+?\\d{1,3}[\\s\\-]?)?(\\(?\\d{3}\\)?[\\s\\-]?)?[\\d\\s\\-]{7,13}"
        ).matcher(text);
        return m.find() ? m.group().trim() : "Not found";
    }

    private String extractExperience(String text) {
        Matcher m = Pattern.compile("(\\d+)\\s*\\+?\\s*years?\\s*(of\\s+)?experience",
                Pattern.CASE_INSENSITIVE).matcher(text);
        if (m.find()) return m.group().trim();
        if (text.toLowerCase().contains("fresher") || text.toLowerCase().contains("entry level"))
            return "Fresher";
        return "Not specified";
    }
}

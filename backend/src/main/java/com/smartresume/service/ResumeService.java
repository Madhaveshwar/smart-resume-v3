package com.smartresume.service;

import com.smartresume.model.Resume;
import com.smartresume.service.MatchingService.MatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeService {

    private final TikaParserService tikaParser;
    private final MatchingService matchingService;

    // 🔥 TEMP STORAGE (instead of DB)
    private final List<Resume> memoryStorage = new ArrayList<>();

    @Autowired
    public ResumeService(TikaParserService tikaParser,
                         MatchingService matchingService) {
        this.tikaParser = tikaParser;
        this.matchingService = matchingService;
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

                // ✅ store in memory instead of DB
                memoryStorage.add(resume);
                results.add(resume);

            } catch (Exception ignored) {}
        }

        results.sort(Comparator.comparingInt(Resume::getMatchPercentage).reversed());
        return results;
    }

    public List<Resume> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return memoryStorage.stream()
                    .sorted(Comparator.comparingInt(Resume::getMatchPercentage).reversed())
                    .toList();
        }

        return memoryStorage.stream()
                .filter(r -> r.getRawText().toLowerCase().contains(keyword.toLowerCase()))
                .sorted(Comparator.comparingInt(Resume::getMatchPercentage).reversed())
                .toList();
    }

    public List<Resume> getAllResumes() {
        return memoryStorage.stream()
                .sorted(Comparator.comparingInt(Resume::getMatchPercentage).reversed())
                .toList();
    }

    public void clearAll() {
        memoryStorage.clear();
    }

    public List<String> getJobTitles() {
        return matchingService.getJobTitles();
    }

    // ── Build resume ─────────────────────────
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

        int pct = Math.min(100, allSkills.size() * 5);

        r.setMatchPercentage(pct);
        r.setScore(pct);

        return r;
    }

    // ── Helpers ─────────────────────────
    private String extractName(String text) {
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] words = line.split("\\s+");
            if (words.length >= 2 && words.length <= 5 &&
                    Character.isUpperCase(words[0].charAt(0))) {
                return line;
            }
        }
        return "Unknown";
    }

    private String extractEmail(String text) {
        Matcher m = Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}")
                .matcher(text);
        return m.find() ? m.group() : "Not found";
    }

    private String extractPhone(String text) {
        Matcher m = Pattern.compile("(\\+?\\d{1,3}[\\s\\-]?)?(\\(?\\d{3}\\)?[\\s\\-]?)?[\\d\\s\\-]{7,13}")
                .matcher(text);
        return m.find() ? m.group().trim() : "Not found";
    }

    private String extractExperience(String text) {
        Matcher m = Pattern.compile("(\\d+)\\s*\\+?\\s*years?",
                Pattern.CASE_INSENSITIVE).matcher(text);
        if (m.find()) return m.group();

        if (text.toLowerCase().contains("fresher"))
            return "Fresher";

        return "Not specified";
    }
}
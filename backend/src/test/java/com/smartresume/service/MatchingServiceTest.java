package com.smartresume.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingServiceTest {

    private MatchingService service;

    @BeforeEach
    void setUp() { service = new MatchingService(); }

    @Test
    void weightedScore_shouldIncreaseWithMoreMatches() {
        String text = "Java Spring Boot MySQL Docker Kubernetes";
        int score1 = service.calculateWeightedScore(text, Arrays.asList("java"));
        int score2 = service.calculateWeightedScore(text, Arrays.asList("java", "spring boot", "mysql"));
        assertTrue(score2 > score1);
    }

    @Test
    void matchAgainstJobTitle_softwareEngineer() {
        String text = "Experienced Java developer with Spring Boot, MySQL, REST API, and Git skills.";
        int pct = service.matchAgainstJobTitle(text, "Software Engineer");
        assertTrue(pct > 50, "Expected >50% match, got " + pct);
    }

    @Test
    void matchAgainstKeywords_allMatch() {
        int pct = service.matchAgainstKeywords("java spring boot mysql", "java spring boot mysql");
        assertEquals(100, pct);
    }

    @Test
    void cosineSimilarity_identicalTexts() {
        String text = "java spring boot mysql docker kubernetes";
        int sim = service.cosineSimilarity(text, text);
        assertTrue(sim > 80, "Identical texts should have high similarity, got " + sim);
    }

    @Test
    void cosineSimilarity_unrelatedTexts() {
        int sim = service.cosineSimilarity("java spring boot", "cooking recipes baking");
        assertEquals(0, sim);
    }

    @Test
    void extractSkills_shouldFindKnownSkills() {
        String text = "I have experience with Java, Spring Boot, MySQL and Docker.";
        List<String> skills = service.extractSkills(text);
        assertTrue(skills.contains("java"));
        assertTrue(skills.contains("spring boot"));
        assertTrue(skills.contains("mysql"));
        assertTrue(skills.contains("docker"));
    }

    @Test
    void suggestJobs_shouldReturnRelevantTitles() {
        List<String> skills = Arrays.asList("spring boot", "java", "mysql");
        List<String> jobs = service.suggestJobs(skills);
        assertFalse(jobs.isEmpty());
        assertTrue(jobs.contains("Software Engineer") || jobs.contains("Backend Developer"));
    }

    @Test
    void getJobTitles_shouldBeSorted() {
        List<String> titles = service.getJobTitles();
        for (int i = 0; i < titles.size() - 1; i++) {
            assertTrue(titles.get(i).compareTo(titles.get(i + 1)) <= 0);
        }
    }
}

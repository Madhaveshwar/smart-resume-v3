package com.smartresume.repository;

import com.smartresume.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository — auto-generates all CRUD SQL.
 * Custom queries allow skill-based search directly in the database.
 */
@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    /** Find all resumes ordered by match percentage descending. */
    List<Resume> findAllByOrderByMatchPercentageDesc();

    /** Find resumes whose skills column contains a given keyword (case-insensitive). */
    @Query("SELECT r FROM Resume r WHERE LOWER(r.skills) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Resume> findBySkillContaining(@Param("keyword") String keyword);

    /** Find resumes whose raw text contains a keyword — broad full-text fallback. */
    @Query("SELECT r FROM Resume r WHERE LOWER(r.rawText) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY r.matchPercentage DESC")
    List<Resume> searchByKeyword(@Param("keyword") String keyword);

    /** Delete all resumes — used by the "clear all" admin action. */
    void deleteAll();
}

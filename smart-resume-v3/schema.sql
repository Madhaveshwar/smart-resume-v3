-- Smart Resume ATS — Schema v3 (Industry Level)
-- Run once to create / update the database

CREATE DATABASE IF NOT EXISTS smart_resume_db;
USE smart_resume_db;

CREATE TABLE IF NOT EXISTS resumes (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(200),
    email            VARCHAR(200),
    phone            VARCHAR(50),
    experience       VARCHAR(100),
    file_name        VARCHAR(255),
    file_path        TEXT,
    raw_text         LONGTEXT,
    skills           TEXT,
    matched_skills   TEXT,
    missing_skills   TEXT,
    suggestions      LONGTEXT,
    recommended_roles TEXT,
    suggested_jobs   TEXT,
    score            INT DEFAULT 0,
    match_percentage INT DEFAULT 0,
    uploaded_at      DATETIME
);

-- If upgrading from v2: add new columns safely
ALTER TABLE resumes ADD COLUMN IF NOT EXISTS matched_skills   TEXT;
ALTER TABLE resumes ADD COLUMN IF NOT EXISTS missing_skills   TEXT;
ALTER TABLE resumes ADD COLUMN IF NOT EXISTS suggestions      LONGTEXT;
ALTER TABLE resumes ADD COLUMN IF NOT EXISTS recommended_roles TEXT;

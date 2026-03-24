# Smart Resume Filtering System v2.0
**Java Full Stack · Spring Boot · MySQL · Apache Tika · TF-IDF**

---

## What's New vs v1

| Feature | v1 (old) | v2 (this project) |
|---|---|---|
| File parsing | PDFBox (PDF only) | **Apache Tika** (PDF + DOCX + DOC + TXT) |
| Storage | No database | **MySQL** via Spring Data JPA |
| Matching | Simple keyword % | **Weighted scoring + TF-IDF cosine similarity** |
| Search | None | **Keyword search** over stored resumes |
| Admin | None | **Dashboard** with clear-all |
| Job description | Not supported | **Full JD matching** with NLP blend |
| Experience | Not extracted | **Auto-detected** from resume text |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17 · Spring Boot 3.2 |
| Database | MySQL 8+ · Spring Data JPA · Hibernate |
| Text extraction | Apache Tika 2.9.1 (PDF, DOCX, DOC, TXT) |
| NLP Matching | TF-IDF · Cosine Similarity · Weighted keywords |
| Templates | Thymeleaf |
| Frontend | HTML5 · Tailwind CSS · Vanilla JS |
| Build | Maven |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

---

## Setup

### 1. Create MySQL database
```sql
CREATE DATABASE smart_resume_db;
```
Or just run the app — `ddl-auto=update` creates the table automatically.

### 2. Configure credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_resume_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password_here
```

### 3. Build and run
```bash
mvn clean package -DskipTests
mvn spring-boot:run
```
Open **http://localhost:8080**

---

## Project Structure

```
smart-resume-filter/
├── pom.xml
├── schema.sql                          ← Optional manual DB setup
└── src/
    ├── main/java/com/smartresume/
    │   ├── SmartResumeApplication.java ← Entry point
    │   ├── controller/
    │   │   └── ResumeController.java   ← All web + REST routes
    │   ├── service/
    │   │   ├── ResumeService.java      ← Orchestration (parse + match + save)
    │   │   ├── TikaParserService.java  ← Apache Tika text extraction
    │   │   └── MatchingService.java    ← Weighted + TF-IDF matching
    │   ├── model/
    │   │   └── Resume.java             ← JPA entity (maps to MySQL)
    │   ├── repository/
    │   │   └── ResumeRepository.java   ← Spring Data JPA queries
    │   └── config/
    │       └── WebConfig.java
    └── resources/
        ├── application.properties
        ├── templates/index.html        ← Thymeleaf UI (Upload / Search / Dashboard)
        └── static/css + js
```

---

## Matching Logic

### 1. Weighted keyword matching
Each skill has a weight (e.g. Spring Boot = 20, Java = 10, HTML = 6).
Score = sum of weights of matched skills.

### 2. TF-IDF Cosine Similarity
When a Job Description is pasted:
- Resume and JD are both tokenised into TF-IDF vectors
- Cosine similarity is computed
- Final score = 50% job-title match + 30% keyword match + 20% cosine

### 3. Job title matching
Predefined skill sets per role. Match % = weighted skills present / total required weight.

---

## REST API

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/job-titles` | List of available job titles |
| POST | `/api/process` | Upload + process → JSON results |
| GET | `/api/search?keyword=Java` | Keyword search → JSON |

### curl example
```bash
curl -X POST http://localhost:8080/api/process \
  -F "job_title=Backend Developer" \
  -F "job_desc=We need Java Spring Boot MySQL Docker" \
  -F "pdf_docs=@resume.pdf"
```

---

## Database Schema

```sql
CREATE TABLE resumes (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(200),
    email            VARCHAR(200),
    phone            VARCHAR(50),
    skills           TEXT,
    experience       VARCHAR(100),
    file_name        VARCHAR(255),
    file_path        TEXT,
    raw_text         LONGTEXT,
    score            INT,
    match_percentage INT,
    suggested_jobs   TEXT,
    uploaded_at      DATETIME
);
```

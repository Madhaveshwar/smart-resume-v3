# Smart Resume ATS — Frontend / Backend Separation

## Architecture Overview

```
[Browser]  ←→  [Frontend :3000]  ←→  [Spring Boot API :8080]  ←→  [MySQL :3306]
  HTML/CSS/JS      static files          REST JSON API             smart_resume_db
```

## Folder Structure

```
smart-resume-separated/
├── README.md
├── backend/                          ← Spring Boot REST API (Java 17)
│   ├── pom.xml
│   ├── schema.sql
│   └── src/main/
│       ├── java/com/smartresume/
│       │   ├── SmartResumeApplication.java
│       │   ├── controller/ResumeController.java   ← REST-only (/api/*)
│       │   ├── service/
│       │   │   ├── MatchingService.java
│       │   │   ├── ResumeService.java
│       │   │   └── TikaParserService.java
│       │   ├── model/Resume.java
│       │   ├── repository/ResumeRepository.java
│       │   └── config/WebConfig.java              ← CORS enabled
│       └── resources/application.properties
│
└── frontend/                         ← Static HTML/CSS/JS
    ├── package.json
    ├── index.html                    ← Upload & Results tab
    ├── search.html                   ← Search tab
    ├── dashboard.html                ← Admin Dashboard tab
    └── assets/
        ├── css/style.css
        └── js/
            ├── config.js             ← API_BASE URL (change if needed)
            ├── components.js         ← Shared resume card renderer
            ├── upload.js             ← Upload page logic
            ├── search.js             ← Search page logic
            └── dashboard.js          ← Dashboard page logic
```

---

## Prerequisites

| Tool      | Version  |
|-----------|----------|
| Java      | 17+      |
| Maven     | 3.8+     |
| MySQL     | 8.0+     |
| Node.js   | 16+ (optional, for `serve`) |

---

## Step-by-Step Setup

### Step 1 — MySQL Database

```sql
-- Option A: Run schema manually
mysql -u root -p < backend/schema.sql

-- Option B: Let Spring Boot auto-create it (ddl-auto=update handles this)
```

### Step 2 — Configure Backend

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_resume_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE   ← change this
```

### Step 3 — (Optional) Configure Frontend API URL

Edit `frontend/assets/js/config.js` if your backend runs on a different host/port:

```js
const API_BASE = "http://localhost:8080";  // default
```

---

## Running the Project

### Terminal 1 — Start Backend

```bash
cd backend
mvn clean package -DskipTests
mvn spring-boot:run
```

Backend starts at **http://localhost:8080**

### Terminal 2 — Start Frontend

```bash
cd frontend

# Option A: Node serve (recommended)
npm install
npm start
# → http://localhost:3000

# Option B: Python
python3 -m http.server 3000
# → http://localhost:3000

# Option C: VS Code Live Server
# Right-click index.html → "Open with Live Server"
```

Open **http://localhost:3000** in your browser.

---

## REST API Reference

| Method | Endpoint              | Description                        |
|--------|-----------------------|------------------------------------|
| GET    | `/api/job-titles`     | List of 20 supported job titles    |
| POST   | `/api/process`        | Upload resumes → ATS analysis JSON |
| GET    | `/api/search?keyword=`| Keyword search over stored resumes |
| GET    | `/api/dashboard`      | All stored resumes + total count   |
| POST   | `/api/clear`          | Delete all resumes from database   |

### Example curl

```bash
curl -X POST http://localhost:8080/api/process \
  -F "job_title=Backend Developer" \
  -F "job_desc=We need Java Spring Boot MySQL Docker" \
  -F "pdf_docs=@resume.pdf"
```

---

## Changes from Original

| File | Change |
|------|--------|
| `pom.xml` | Removed `spring-boot-starter-thymeleaf` dependency |
| `controller/ResumeController.java` | Removed Thymeleaf `@Controller` + view routes; pure `@RestController` under `/api/*` |
| `config/WebConfig.java` | Updated CORS to allow `localhost:3000` and `localhost:5500` |
| `application.properties` | Removed all `spring.thymeleaf.*` settings |
| `frontend/index.html` | Converted from Thymeleaf template to plain HTML + fetch() API calls |
| `frontend/search.html` | New standalone page (was a section in `index.html`) |
| `frontend/dashboard.html` | New standalone page (was a section in `index.html`) |
| `frontend/assets/js/config.js` | New — centralised API base URL |
| `frontend/assets/js/components.js` | New — shared resume card renderer (replaces Thymeleaf fragment) |
| `frontend/assets/js/upload.js` | Replaces original `app.js` — adds fetch() upload logic |
| `frontend/assets/js/search.js` | New — search page fetch() logic |
| `frontend/assets/js/dashboard.js` | New — dashboard fetch() + clear logic |

All core business logic (MatchingService, ResumeService, TikaParserService, Resume model, ResumeRepository) is **100% unchanged**.

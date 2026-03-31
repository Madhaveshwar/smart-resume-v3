package com.smartresume.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Industry-level ATS Matching Service
 * - 20 real-world job roles with required skills
 * - Matched / Missing skills breakdown
 * - Personalised improvement suggestions per missing skill
 * - Alternative role recommendations for low-match candidates
 */
@Service
public class MatchingService {

    private static final Map<String, Integer> SKILL_WEIGHTS = new LinkedHashMap<>();

    static {
        SKILL_WEIGHTS.put("java", 10); SKILL_WEIGHTS.put("python", 10);
        SKILL_WEIGHTS.put("javascript", 8); SKILL_WEIGHTS.put("typescript", 8);
        SKILL_WEIGHTS.put("c++", 8); SKILL_WEIGHTS.put("c#", 8);
        SKILL_WEIGHTS.put("kotlin", 8); SKILL_WEIGHTS.put("scala", 8);
        SKILL_WEIGHTS.put("go", 7); SKILL_WEIGHTS.put("rust", 7);
        SKILL_WEIGHTS.put("swift", 8); SKILL_WEIGHTS.put("dart", 7);
        SKILL_WEIGHTS.put("r", 6); SKILL_WEIGHTS.put("bash", 6);
        SKILL_WEIGHTS.put("shell scripting", 7); SKILL_WEIGHTS.put("php", 7);
        SKILL_WEIGHTS.put("ruby", 7);
        SKILL_WEIGHTS.put("spring boot", 20); SKILL_WEIGHTS.put("spring", 12);
        SKILL_WEIGHTS.put("react", 15); SKILL_WEIGHTS.put("angular", 15);
        SKILL_WEIGHTS.put("vue", 12); SKILL_WEIGHTS.put("node.js", 12);
        SKILL_WEIGHTS.put("django", 12); SKILL_WEIGHTS.put("flask", 10);
        SKILL_WEIGHTS.put("fastapi", 10); SKILL_WEIGHTS.put("hibernate", 12);
        SKILL_WEIGHTS.put("express.js", 10); SKILL_WEIGHTS.put("next.js", 12);
        SKILL_WEIGHTS.put("flutter", 12); SKILL_WEIGHTS.put("react native", 12);
        SKILL_WEIGHTS.put("html", 6); SKILL_WEIGHTS.put("css", 6);
        SKILL_WEIGHTS.put("rest api", 12); SKILL_WEIGHTS.put("graphql", 12);
        SKILL_WEIGHTS.put("microservices", 15); SKILL_WEIGHTS.put("responsive design", 8);
        SKILL_WEIGHTS.put("tailwind", 7); SKILL_WEIGHTS.put("bootstrap", 6);
        SKILL_WEIGHTS.put("figma", 10); SKILL_WEIGHTS.put("adobe xd", 10);
        SKILL_WEIGHTS.put("sketch", 10);
        SKILL_WEIGHTS.put("mysql", 10); SKILL_WEIGHTS.put("sql", 8);
        SKILL_WEIGHTS.put("postgresql", 10); SKILL_WEIGHTS.put("mongodb", 10);
        SKILL_WEIGHTS.put("redis", 10); SKILL_WEIGHTS.put("oracle", 8);
        SKILL_WEIGHTS.put("nosql", 8); SKILL_WEIGHTS.put("cassandra", 10);
        SKILL_WEIGHTS.put("elasticsearch", 10);
        SKILL_WEIGHTS.put("database administration", 12); SKILL_WEIGHTS.put("database design", 10);
        SKILL_WEIGHTS.put("query optimization", 10);
        SKILL_WEIGHTS.put("aws", 15); SKILL_WEIGHTS.put("azure", 15);
        SKILL_WEIGHTS.put("gcp", 15); SKILL_WEIGHTS.put("docker", 12);
        SKILL_WEIGHTS.put("kubernetes", 15); SKILL_WEIGHTS.put("jenkins", 10);
        SKILL_WEIGHTS.put("git", 8); SKILL_WEIGHTS.put("linux", 8);
        SKILL_WEIGHTS.put("ci/cd", 12); SKILL_WEIGHTS.put("terraform", 12);
        SKILL_WEIGHTS.put("ansible", 10); SKILL_WEIGHTS.put("prometheus", 8);
        SKILL_WEIGHTS.put("grafana", 8); SKILL_WEIGHTS.put("networking", 10);
        SKILL_WEIGHTS.put("virtualization", 8); SKILL_WEIGHTS.put("cloud architecture", 14);
        SKILL_WEIGHTS.put("serverless", 10);
        SKILL_WEIGHTS.put("machine learning", 18); SKILL_WEIGHTS.put("deep learning", 18);
        SKILL_WEIGHTS.put("data analysis", 14); SKILL_WEIGHTS.put("tensorflow", 15);
        SKILL_WEIGHTS.put("pytorch", 15); SKILL_WEIGHTS.put("nlp", 15);
        SKILL_WEIGHTS.put("statistics", 12); SKILL_WEIGHTS.put("pandas", 10);
        SKILL_WEIGHTS.put("numpy", 10); SKILL_WEIGHTS.put("scikit-learn", 12);
        SKILL_WEIGHTS.put("data visualization", 10); SKILL_WEIGHTS.put("tableau", 10);
        SKILL_WEIGHTS.put("power bi", 10); SKILL_WEIGHTS.put("computer vision", 15);
        SKILL_WEIGHTS.put("feature engineering", 12); SKILL_WEIGHTS.put("model deployment", 12);
        SKILL_WEIGHTS.put("large language models", 18); SKILL_WEIGHTS.put("prompt engineering", 12);
        SKILL_WEIGHTS.put("cybersecurity", 15); SKILL_WEIGHTS.put("penetration testing", 15);
        SKILL_WEIGHTS.put("siem", 12); SKILL_WEIGHTS.put("firewall", 10);
        SKILL_WEIGHTS.put("encryption", 10); SKILL_WEIGHTS.put("vulnerability assessment", 12);
        SKILL_WEIGHTS.put("ethical hacking", 12); SKILL_WEIGHTS.put("owasp", 10);
        SKILL_WEIGHTS.put("network security", 12);
        SKILL_WEIGHTS.put("android", 12); SKILL_WEIGHTS.put("ios", 12);
        SKILL_WEIGHTS.put("xcode", 8); SKILL_WEIGHTS.put("android studio", 8);
        SKILL_WEIGHTS.put("selenium", 12); SKILL_WEIGHTS.put("junit", 10);
        SKILL_WEIGHTS.put("testng", 10); SKILL_WEIGHTS.put("jmeter", 10);
        SKILL_WEIGHTS.put("postman", 8); SKILL_WEIGHTS.put("api testing", 10);
        SKILL_WEIGHTS.put("test automation", 12); SKILL_WEIGHTS.put("test planning", 10);
        SKILL_WEIGHTS.put("manual testing", 8); SKILL_WEIGHTS.put("performance testing", 10);
        SKILL_WEIGHTS.put("windows server", 10); SKILL_WEIGHTS.put("active directory", 10);
        SKILL_WEIGHTS.put("system administration", 12); SKILL_WEIGHTS.put("troubleshooting", 8);
        SKILL_WEIGHTS.put("project management", 14); SKILL_WEIGHTS.put("agile", 10);
        SKILL_WEIGHTS.put("scrum", 10); SKILL_WEIGHTS.put("jira", 8);
        SKILL_WEIGHTS.put("communication", 8); SKILL_WEIGHTS.put("leadership", 8);
        SKILL_WEIGHTS.put("stakeholder management", 10); SKILL_WEIGHTS.put("business analysis", 12);
        SKILL_WEIGHTS.put("requirements gathering", 10); SKILL_WEIGHTS.put("data modeling", 10);
        SKILL_WEIGHTS.put("product roadmap", 10); SKILL_WEIGHTS.put("product management", 14);
        SKILL_WEIGHTS.put("user research", 10); SKILL_WEIGHTS.put("market analysis", 10);
        SKILL_WEIGHTS.put("a/b testing", 10); SKILL_WEIGHTS.put("ux design", 12);
        SKILL_WEIGHTS.put("wireframing", 10); SKILL_WEIGHTS.put("prototyping", 10);
        SKILL_WEIGHTS.put("ui design", 10); SKILL_WEIGHTS.put("user testing", 10);
        SKILL_WEIGHTS.put("design systems", 10);
        SKILL_WEIGHTS.put("apache tika", 12); SKILL_WEIGHTS.put("apache kafka", 12);
        SKILL_WEIGHTS.put("thymeleaf", 8); SKILL_WEIGHTS.put("maven", 6);
    }

    private static final Map<String, List<String>> JOB_SKILLS = new LinkedHashMap<>();

    static {
        JOB_SKILLS.put("Full Stack Developer",
                Arrays.asList("java","spring boot","react","javascript","html","css","mysql","rest api","git","node.js"));
        JOB_SKILLS.put("Backend Developer",
                Arrays.asList("java","spring boot","mysql","rest api","microservices","docker","git","postgresql","redis"));
        JOB_SKILLS.put("Frontend Developer",
                Arrays.asList("html","css","javascript","react","typescript","responsive design","git","rest api","figma"));
        JOB_SKILLS.put("Web Developer",
                Arrays.asList("html","css","javascript","react","node.js","responsive design","git","bootstrap"));
        JOB_SKILLS.put("Software Engineer",
                Arrays.asList("java","python","git","sql","rest api","agile","linux","data structures","algorithms"));
        JOB_SKILLS.put("Data Scientist",
                Arrays.asList("python","machine learning","deep learning","statistics","pandas","numpy","sql","scikit-learn","data visualization","tensorflow"));
        JOB_SKILLS.put("Data Analyst",
                Arrays.asList("sql","python","data analysis","tableau","power bi","statistics","data visualization","mysql","excel"));
        JOB_SKILLS.put("Machine Learning Engineer",
                Arrays.asList("python","machine learning","deep learning","tensorflow","pytorch","scikit-learn","feature engineering","model deployment","numpy","pandas"));
        JOB_SKILLS.put("AI Engineer",
                Arrays.asList("python","machine learning","deep learning","large language models","tensorflow","pytorch","nlp","prompt engineering","rest api","docker"));
        JOB_SKILLS.put("DevOps Engineer",
                Arrays.asList("docker","kubernetes","aws","ci/cd","linux","terraform","jenkins","ansible","git","shell scripting"));
        JOB_SKILLS.put("Cloud Engineer",
                Arrays.asList("aws","azure","gcp","networking","virtualization","docker","kubernetes","terraform","linux","cloud architecture"));
        JOB_SKILLS.put("Cybersecurity Analyst",
                Arrays.asList("cybersecurity","penetration testing","siem","firewall","encryption","vulnerability assessment","ethical hacking","owasp","network security","linux"));
        JOB_SKILLS.put("Mobile App Developer",
                Arrays.asList("android","ios","kotlin","swift","flutter","react native","rest api","git","android studio","xcode"));
        JOB_SKILLS.put("UI/UX Designer",
                Arrays.asList("figma","adobe xd","sketch","ux design","wireframing","prototyping","user research","ui design","user testing","design systems"));
        JOB_SKILLS.put("QA Engineer",
                Arrays.asList("selenium","junit","testng","jmeter","postman","api testing","test automation","test planning","manual testing","performance testing"));
        JOB_SKILLS.put("System Administrator",
                Arrays.asList("linux","windows server","active directory","networking","shell scripting","virtualization","system administration","troubleshooting","aws","ansible"));
        JOB_SKILLS.put("Database Administrator",
                Arrays.asList("mysql","postgresql","oracle","sql","database administration","database design","query optimization","redis","mongodb","data modeling"));
        JOB_SKILLS.put("Business Analyst",
                Arrays.asList("business analysis","requirements gathering","sql","data analysis","agile","jira","stakeholder management","data modeling","tableau","communication"));
        JOB_SKILLS.put("Project Manager",
                Arrays.asList("project management","agile","scrum","jira","communication","leadership","stakeholder management","risk management","ms project","budget management"));
        JOB_SKILLS.put("Product Manager",
                Arrays.asList("product management","product roadmap","user research","market analysis","agile","a/b testing","jira","stakeholder management","data analysis","communication"));
    }

    private static final Map<String, String> SKILL_SUGGESTIONS = new LinkedHashMap<>();

    static {
        SKILL_SUGGESTIONS.put("java","Learn Java Core and OOP concepts, then build REST APIs with Spring Boot");
        SKILL_SUGGESTIONS.put("python","Practice Python on LeetCode and build real-world projects using automation or data tasks");
        SKILL_SUGGESTIONS.put("javascript","Build interactive UI projects with Vanilla JS and then explore ES6+ features");
        SKILL_SUGGESTIONS.put("typescript","Learn TypeScript basics, type system, and integrate it with React or Node.js projects");
        SKILL_SUGGESTIONS.put("kotlin","Start Android development with Kotlin by building 2-3 simple apps");
        SKILL_SUGGESTIONS.put("swift","Learn Swift basics and build iOS apps using Xcode and deploy to TestFlight");
        SKILL_SUGGESTIONS.put("flutter","Build cross-platform mobile apps with Flutter and Dart — start with a todo or weather app");
        SKILL_SUGGESTIONS.put("react native","Learn React Native and build 2 production-style cross-platform apps");
        SKILL_SUGGESTIONS.put("go","Learn Go basics and build a REST API server — Go is great for cloud-native backends");
        SKILL_SUGGESTIONS.put("rust","Study Rust's ownership model and build a small systems utility project");
        SKILL_SUGGESTIONS.put("spring boot","Build a full REST API with Spring Boot including auth, CRUD, and database integration");
        SKILL_SUGGESTIONS.put("react","Build frontend projects using React — try a portfolio site or dashboard and deploy to Vercel");
        SKILL_SUGGESTIONS.put("angular","Complete Angular's official tour-of-heroes tutorial, then build a real CRUD app");
        SKILL_SUGGESTIONS.put("vue","Learn Vue.js basics and build a dynamic SPA with Vue Router and Vuex state management");
        SKILL_SUGGESTIONS.put("node.js","Build a Node.js REST API with Express.js, connect it to MongoDB, and deploy on Heroku");
        SKILL_SUGGESTIONS.put("django","Build a web app with Django including authentication, ORM, and REST API using DRF");
        SKILL_SUGGESTIONS.put("flask","Create a Python Flask API with database integration and deploy it on a cloud platform");
        SKILL_SUGGESTIONS.put("fastapi","Learn FastAPI to build modern async REST APIs in Python with auto-generated docs");
        SKILL_SUGGESTIONS.put("html","Practice HTML5 semantic tags by building real websites — focus on accessibility");
        SKILL_SUGGESTIONS.put("css","Master CSS Flexbox, Grid, and animations — rebuild popular website UIs for practice");
        SKILL_SUGGESTIONS.put("responsive design","Study CSS media queries and Flexbox/Grid to build fully responsive layouts from scratch");
        SKILL_SUGGESTIONS.put("sql","Improve SQL by solving 50+ queries on LeetCode/HackerRank — practice JOINs, subqueries, and window functions");
        SKILL_SUGGESTIONS.put("mysql","Set up MySQL locally and practise CRUD, indexing, stored procedures, and transactions");
        SKILL_SUGGESTIONS.put("postgresql","Learn PostgreSQL-specific features like JSONB, window functions, and advanced indexing");
        SKILL_SUGGESTIONS.put("mongodb","Learn MongoDB CRUD, aggregation pipeline, and indexing — build a project using the Atlas free tier");
        SKILL_SUGGESTIONS.put("redis","Learn Redis for caching and session management — integrate it with a Spring Boot or Node.js app");
        SKILL_SUGGESTIONS.put("docker","Learn Docker containerisation basics and Dockerise an existing project with a multi-stage build");
        SKILL_SUGGESTIONS.put("kubernetes","Deploy a containerised app to a local Minikube cluster then to a managed cloud service");
        SKILL_SUGGESTIONS.put("aws","Get AWS Cloud Practitioner certified — practice with EC2, S3, Lambda, and RDS on the free tier");
        SKILL_SUGGESTIONS.put("azure","Complete AZ-900 Azure Fundamentals certification and deploy an app on Azure App Service");
        SKILL_SUGGESTIONS.put("gcp","Explore Google Cloud on Qwiklabs and earn the Associate Cloud Engineer certification");
        SKILL_SUGGESTIONS.put("ci/cd","Set up a CI/CD pipeline using GitHub Actions or Jenkins to automate build, test, and deploy");
        SKILL_SUGGESTIONS.put("terraform","Learn Infrastructure as Code with Terraform — provision AWS resources using Terraform scripts");
        SKILL_SUGGESTIONS.put("ansible","Learn Ansible playbooks to automate server configuration and application deployment");
        SKILL_SUGGESTIONS.put("linux","Practice Linux command-line skills daily — learn process, file, and network management on Ubuntu");
        SKILL_SUGGESTIONS.put("git","Master Git branching strategies (Gitflow), rebasing, and collaborative workflows on GitHub");
        SKILL_SUGGESTIONS.put("machine learning","Learn Machine Learning using Python and Scikit-learn and build 2 end-to-end ML projects");
        SKILL_SUGGESTIONS.put("deep learning","Study Deep Learning with TensorFlow or PyTorch — build image classification and NLP projects");
        SKILL_SUGGESTIONS.put("tensorflow","Complete the TensorFlow Developer certification and build models for real-world datasets");
        SKILL_SUGGESTIONS.put("pytorch","Learn PyTorch by implementing models — start with image classification on CIFAR-10");
        SKILL_SUGGESTIONS.put("scikit-learn","Build ML pipelines with Scikit-learn including preprocessing, model selection, and evaluation");
        SKILL_SUGGESTIONS.put("pandas","Practise Pandas data manipulation with Kaggle datasets — master groupby, merge, and pivot tables");
        SKILL_SUGGESTIONS.put("numpy","Study NumPy arrays, broadcasting, and linear algebra operations through practice exercises");
        SKILL_SUGGESTIONS.put("statistics","Learn probability, hypothesis testing, and regression analysis — apply them to real datasets");
        SKILL_SUGGESTIONS.put("data analysis","Practise data analysis on Kaggle — work through full EDA pipeline on 3 different datasets");
        SKILL_SUGGESTIONS.put("data visualization","Learn Matplotlib, Seaborn, and Plotly to create insightful charts and dashboards");
        SKILL_SUGGESTIONS.put("tableau","Learn Tableau through free public training and build 3 interactive dashboards in Tableau Public");
        SKILL_SUGGESTIONS.put("power bi","Complete Microsoft's free Power BI learning path and build a business dashboard with real data");
        SKILL_SUGGESTIONS.put("nlp","Study NLP basics — implement text classification, sentiment analysis, and build a chatbot");
        SKILL_SUGGESTIONS.put("large language models","Learn LLM fundamentals, explore OpenAI/HuggingFace APIs, and build an LLM-powered app");
        SKILL_SUGGESTIONS.put("prompt engineering","Practise prompt engineering techniques — few-shot, chain-of-thought, and RAG patterns");
        SKILL_SUGGESTIONS.put("cybersecurity","Get CompTIA Security+ certified and set up a home lab to practise security techniques");
        SKILL_SUGGESTIONS.put("penetration testing","Learn ethical hacking on TryHackMe or HackTheBox and earn CEH or OSCP certification");
        SKILL_SUGGESTIONS.put("vulnerability assessment","Learn vulnerability scanning using Nessus, OpenVAS, and Nmap with real targets");
        SKILL_SUGGESTIONS.put("ethical hacking","Start on TryHackMe — complete learning paths in web fundamentals and penetration testing");
        SKILL_SUGGESTIONS.put("network security","Study OSI model, firewalls, IDS/IPS, and practise packet analysis with Wireshark");
        SKILL_SUGGESTIONS.put("siem","Learn SIEM basics with Splunk or IBM QRadar — practise log analysis and alert correlation");
        SKILL_SUGGESTIONS.put("figma","Learn Figma by redesigning a popular app — focus on components, auto-layout, and prototyping");
        SKILL_SUGGESTIONS.put("adobe xd","Complete Adobe XD tutorials and design a mobile app prototype with interactive clickthroughs");
        SKILL_SUGGESTIONS.put("ux design","Study the UX design process — user research, personas, journey maps, and usability testing");
        SKILL_SUGGESTIONS.put("wireframing","Create wireframes for 3 app concepts — practise low-fi and hi-fi in Figma or Balsamiq");
        SKILL_SUGGESTIONS.put("prototyping","Build interactive prototypes in Figma and conduct user testing sessions to gather feedback");
        SKILL_SUGGESTIONS.put("user research","Learn user interview, survey, and card-sorting techniques to inform data-driven design decisions");
        SKILL_SUGGESTIONS.put("selenium","Build end-to-end test suites using Selenium WebDriver with Java or Python for a real web app");
        SKILL_SUGGESTIONS.put("test automation","Learn TestNG or Pytest frameworks and automate a full regression test suite");
        SKILL_SUGGESTIONS.put("api testing","Learn REST API testing using Postman and automate with RestAssured or pytest");
        SKILL_SUGGESTIONS.put("jmeter","Learn Apache JMeter to create performance test plans and analyse throughput and response times");
        SKILL_SUGGESTIONS.put("project management","Earn PMP or PRINCE2 Foundation certification and practise managing a real side-project");
        SKILL_SUGGESTIONS.put("agile","Study Agile Scrum framework — consider Scrum Master (PSM) certification");
        SKILL_SUGGESTIONS.put("scrum","Learn the Scrum Guide and practise facilitation of sprint ceremonies in your team");
        SKILL_SUGGESTIONS.put("business analysis","Learn the BABOK framework and practise requirements gathering and use-case modelling");
        SKILL_SUGGESTIONS.put("product management","Study product management frameworks (RICE, OKRs) and build a product case study from scratch");
        SKILL_SUGGESTIONS.put("product roadmap","Practise building product roadmaps in tools like Aha! or Notion aligned with user goals");
        SKILL_SUGGESTIONS.put("market analysis","Learn competitive analysis and market sizing — apply them to a product you use daily");
        SKILL_SUGGESTIONS.put("database administration","Learn MySQL/PostgreSQL DBA tasks — backup, recovery, replication, and performance tuning");
        SKILL_SUGGESTIONS.put("database design","Practise ER diagram design, normalisation to 3NF, and build schemas for real-world use cases");
        SKILL_SUGGESTIONS.put("query optimization","Study query execution plans and indexing strategies to optimise slow database queries");
        SKILL_SUGGESTIONS.put("networking","Study CCNA curriculum — TCP/IP, routing protocols, and subnetting with Packet Tracer");
        SKILL_SUGGESTIONS.put("shell scripting","Write Bash scripts to automate repetitive Linux tasks — cron jobs, file processing, monitoring");
        SKILL_SUGGESTIONS.put("android","Build 2 Android apps in Kotlin using Android Studio — publish one to the Google Play Store");
        SKILL_SUGGESTIONS.put("ios","Build 2 iOS apps in Swift using Xcode — submit one to Apple TestFlight for testing");
        SKILL_SUGGESTIONS.put("microservices","Build a microservices architecture with Spring Boot — include service discovery and API gateway");
        SKILL_SUGGESTIONS.put("rest api","Design and implement a RESTful API following best practices — versioning, auth, and documentation");
        SKILL_SUGGESTIONS.put("graphql","Learn GraphQL by building a schema, resolvers, and mutations — replace a REST endpoint");
        SKILL_SUGGESTIONS.put("stakeholder management","Learn communication and negotiation techniques for managing stakeholder expectations");
        SKILL_SUGGESTIONS.put("leadership","Take on team lead responsibilities and practise delegation, mentoring, and conflict resolution");
        SKILL_SUGGESTIONS.put("communication","Improve presentation and written communication — practise technical writing and stakeholder updates");
        SKILL_SUGGESTIONS.put("data structures","Study arrays, linked lists, trees, graphs, and heaps — practise 50 problems on LeetCode");
        SKILL_SUGGESTIONS.put("algorithms","Study sorting, searching, dynamic programming, and graph algorithms on LeetCode/Codeforces");
        SKILL_SUGGESTIONS.put("excel","Learn advanced Excel — VLOOKUP, pivot tables, macros, and Power Query for data analysis");
        SKILL_SUGGESTIONS.put("risk management","Study risk identification, assessment matrices, and mitigation planning in project management");
        SKILL_SUGGESTIONS.put("ms project","Learn Microsoft Project for scheduling, Gantt charts, resource management, and project tracking");
        SKILL_SUGGESTIONS.put("budget management","Study project budgeting, cost estimation, and earned value management techniques");
        SKILL_SUGGESTIONS.put("a/b testing","Learn A/B testing methodology — hypothesis design, statistical significance, and tools like Optimizely");
        SKILL_SUGGESTIONS.put("user testing","Learn usability testing methods — moderated sessions, think-aloud protocols, and task analysis");
        SKILL_SUGGESTIONS.put("design systems","Build a design system in Figma with components, tokens, and documentation for consistency");
        SKILL_SUGGESTIONS.put("ui design","Study UI design principles — typography, colour theory, spacing, and visual hierarchy");
        SKILL_SUGGESTIONS.put("feature engineering","Practise feature selection, creation, and transformation techniques on Kaggle datasets");
        SKILL_SUGGESTIONS.put("model deployment","Learn to deploy ML models using Flask/FastAPI, Docker, and cloud services like AWS SageMaker");
        SKILL_SUGGESTIONS.put("cloud architecture","Study cloud design patterns — high availability, fault tolerance, and scalability on AWS/Azure");
        SKILL_SUGGESTIONS.put("virtualization","Learn VMware or Hyper-V basics and set up virtual machines for application testing");
        SKILL_SUGGESTIONS.put("active directory","Learn Active Directory administration — users, groups, GPO, and domain management");
        SKILL_SUGGESTIONS.put("windows server","Study Windows Server administration — roles, features, IIS, and Active Directory setup");
        SKILL_SUGGESTIONS.put("system administration","Learn server provisioning, monitoring, patch management, and backup strategies");
        SKILL_SUGGESTIONS.put("troubleshooting","Practise systematic troubleshooting — root cause analysis, logs review, and resolution documentation");
        SKILL_SUGGESTIONS.put("owasp","Study the OWASP Top 10 vulnerabilities and learn to identify and fix them in web apps");
        SKILL_SUGGESTIONS.put("firewall","Learn firewall configuration, rule management, and network segmentation principles");
        SKILL_SUGGESTIONS.put("encryption","Study symmetric and asymmetric encryption, TLS/SSL, and implement encryption in a project");
        SKILL_SUGGESTIONS.put("prometheus","Set up Prometheus monitoring and create custom metrics for application observability");
        SKILL_SUGGESTIONS.put("grafana","Build Grafana dashboards connected to Prometheus to visualise application performance metrics");
        SKILL_SUGGESTIONS.put("junit","Write unit tests with JUnit 5 — practise TDD and achieve 80%+ code coverage in a project");
        SKILL_SUGGESTIONS.put("testng","Learn TestNG annotations, test groups, data providers, and reporting for automated testing");
        SKILL_SUGGESTIONS.put("postman","Master Postman for API testing — create collections, environments, and automated test scripts");
        SKILL_SUGGESTIONS.put("jira","Learn Jira project management — create epics, user stories, sprints, and track velocity");
        SKILL_SUGGESTIONS.put("cassandra","Learn Cassandra data modelling, CQL, and when to use wide-column storage for scalable apps");
        SKILL_SUGGESTIONS.put("elasticsearch","Learn Elasticsearch for full-text search and analytics — build a search feature in a project");
        SKILL_SUGGESTIONS.put("serverless","Build a serverless application using AWS Lambda, API Gateway, and DynamoDB");
        SKILL_SUGGESTIONS.put("computer vision","Build computer vision projects — object detection and image classification with OpenCV and YOLO");
        SKILL_SUGGESTIONS.put("data modeling","Learn dimensional modelling, star/snowflake schemas, and practise with real-world datasets");
        SKILL_SUGGESTIONS.put("requirements gathering","Learn elicitation techniques — interviews, workshops, and document analysis for requirements");
        SKILL_SUGGESTIONS.put("scrum","Study Scrum roles, ceremonies, and artefacts — practise in a real or simulated sprint");
    }

    private static final Map<String, List<String>> SKILL_TO_JOBS = new HashMap<>();

    static {
        SKILL_TO_JOBS.put("java", Arrays.asList("Software Engineer","Backend Developer","Full Stack Developer"));
        SKILL_TO_JOBS.put("spring boot", Arrays.asList("Backend Developer","Full Stack Developer","Software Engineer"));
        SKILL_TO_JOBS.put("python", Arrays.asList("Data Scientist","Machine Learning Engineer","AI Engineer","Backend Developer"));
        SKILL_TO_JOBS.put("machine learning", Arrays.asList("Machine Learning Engineer","Data Scientist","AI Engineer"));
        SKILL_TO_JOBS.put("deep learning", Arrays.asList("AI Engineer","Machine Learning Engineer","Data Scientist"));
        SKILL_TO_JOBS.put("large language models", Arrays.asList("AI Engineer","Machine Learning Engineer"));
        SKILL_TO_JOBS.put("react", Arrays.asList("Frontend Developer","Full Stack Developer","Web Developer"));
        SKILL_TO_JOBS.put("angular", Arrays.asList("Frontend Developer","Full Stack Developer"));
        SKILL_TO_JOBS.put("html", Arrays.asList("Frontend Developer","Web Developer","UI/UX Designer"));
        SKILL_TO_JOBS.put("css", Arrays.asList("Frontend Developer","Web Developer","UI/UX Designer"));
        SKILL_TO_JOBS.put("figma", Arrays.asList("UI/UX Designer","Frontend Developer"));
        SKILL_TO_JOBS.put("ux design", Arrays.asList("UI/UX Designer"));
        SKILL_TO_JOBS.put("docker", Arrays.asList("DevOps Engineer","Cloud Engineer","Backend Developer"));
        SKILL_TO_JOBS.put("kubernetes", Arrays.asList("DevOps Engineer","Cloud Engineer"));
        SKILL_TO_JOBS.put("aws", Arrays.asList("Cloud Engineer","DevOps Engineer","System Administrator"));
        SKILL_TO_JOBS.put("azure", Arrays.asList("Cloud Engineer","System Administrator"));
        SKILL_TO_JOBS.put("gcp", Arrays.asList("Cloud Engineer","Machine Learning Engineer"));
        SKILL_TO_JOBS.put("cybersecurity", Arrays.asList("Cybersecurity Analyst"));
        SKILL_TO_JOBS.put("penetration testing", Arrays.asList("Cybersecurity Analyst"));
        SKILL_TO_JOBS.put("android", Arrays.asList("Mobile App Developer"));
        SKILL_TO_JOBS.put("ios", Arrays.asList("Mobile App Developer"));
        SKILL_TO_JOBS.put("flutter", Arrays.asList("Mobile App Developer"));
        SKILL_TO_JOBS.put("kotlin", Arrays.asList("Mobile App Developer","Backend Developer"));
        SKILL_TO_JOBS.put("selenium", Arrays.asList("QA Engineer"));
        SKILL_TO_JOBS.put("test automation", Arrays.asList("QA Engineer"));
        SKILL_TO_JOBS.put("project management", Arrays.asList("Project Manager","Product Manager"));
        SKILL_TO_JOBS.put("product management", Arrays.asList("Product Manager"));
        SKILL_TO_JOBS.put("agile", Arrays.asList("Project Manager","Business Analyst","Product Manager"));
        SKILL_TO_JOBS.put("business analysis", Arrays.asList("Business Analyst"));
        SKILL_TO_JOBS.put("sql", Arrays.asList("Database Administrator","Data Analyst","Data Scientist","Business Analyst"));
        SKILL_TO_JOBS.put("tableau", Arrays.asList("Data Analyst","Business Analyst"));
        SKILL_TO_JOBS.put("linux", Arrays.asList("System Administrator","DevOps Engineer","Cloud Engineer"));
        SKILL_TO_JOBS.put("database administration", Arrays.asList("Database Administrator"));
        SKILL_TO_JOBS.put("node.js", Arrays.asList("Backend Developer","Full Stack Developer","Web Developer"));
        SKILL_TO_JOBS.put("swift", Arrays.asList("Mobile App Developer"));
        SKILL_TO_JOBS.put("nlp", Arrays.asList("AI Engineer","Machine Learning Engineer","Data Scientist"));
        SKILL_TO_JOBS.put("data analysis", Arrays.asList("Data Analyst","Data Scientist","Business Analyst"));
        SKILL_TO_JOBS.put("statistics", Arrays.asList("Data Scientist","Data Analyst","Machine Learning Engineer"));
        SKILL_TO_JOBS.put("terraform", Arrays.asList("DevOps Engineer","Cloud Engineer"));
        SKILL_TO_JOBS.put("network security", Arrays.asList("Cybersecurity Analyst","System Administrator"));
        SKILL_TO_JOBS.put("scrum", Arrays.asList("Project Manager","Product Manager"));
        SKILL_TO_JOBS.put("stakeholder management", Arrays.asList("Project Manager","Business Analyst","Product Manager"));
    }

    // ── Public API ─────────────────────────────────────────────────

    public MatchResult matchForJobTitle(String resumeText, String jobTitle) {
        List<String> required = JOB_SKILLS.getOrDefault(jobTitle, Collections.emptyList());
        if (required.isEmpty()) return new MatchResult();

        String lower = resumeText.toLowerCase();
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : required) {
            if (lower.contains(skill.toLowerCase())) {
                matched.add(skill);
            } else {
                missing.add(skill);
            }
        }

        int pct = (int) ((matched.size() * 100.0) / required.size());

        List<String> suggestions = missing.stream()
                .map(skill -> SKILL_SUGGESTIONS.getOrDefault(skill,
                        "Learn " + skill + " through online courses and build at least 2 hands-on projects"))
                .collect(Collectors.toList());

        List<String> recommended = Collections.emptyList();
        if (pct < 40) {
            recommended = suggestJobs(matched).stream()
                    .filter(j -> !j.equals(jobTitle))
                    .collect(Collectors.toList());
        }

        return new MatchResult(pct, matched, missing, suggestions, recommended);
    }

    public int matchAgainstJobTitle(String resumeText, String jobTitle) {
        return matchForJobTitle(resumeText, jobTitle).getMatchPercentage();
    }

    public int matchAgainstKeywords(String resumeText, String recruiterInput) {
        if (recruiterInput == null || recruiterInput.isBlank()) return 0;
        List<String> keywords = tokenize(recruiterInput);
        if (keywords.isEmpty()) return 0;
        String lower = resumeText.toLowerCase();
        long matched = keywords.stream().filter(kw -> lower.contains(kw.toLowerCase())).count();
        return (int) ((matched * 100L) / keywords.size());
    }

    public int cosineSimilarity(String resumeText, String jobDescription) {
        if (resumeText == null || jobDescription == null) return 0;
        if (resumeText.isBlank() || jobDescription.isBlank()) return 0;
        Map<String, Double> rv = buildTfIdfVector(tokenize(resumeText));
        Map<String, Double> jv = buildTfIdfVector(tokenize(jobDescription));
        double dot = 0.0;
        for (Map.Entry<String, Double> e : rv.entrySet()) {
            if (jv.containsKey(e.getKey())) dot += e.getValue() * jv.get(e.getKey());
        }
        double m1 = magnitude(rv), m2 = magnitude(jv);
        if (m1 == 0 || m2 == 0) return 0;
        return (int) Math.min(100, (dot / (m1 * m2)) * 100);
    }

    public int calculateWeightedScore(String resumeText, List<String> keywords) {
        if (resumeText == null || resumeText.isBlank()) return 0;
        String lower = resumeText.toLowerCase();
        int score = 0;
        for (String keyword : keywords) {
            String kw = keyword.toLowerCase().trim();
            if (lower.contains(kw)) score += SKILL_WEIGHTS.getOrDefault(kw, 5);
        }
        return score;
    }

    public List<String> extractSkills(String text) {
        if (text == null) return Collections.emptyList();
        String lower = text.toLowerCase();
        return SKILL_WEIGHTS.keySet().stream()
                .filter(lower::contains)
                .collect(Collectors.toList());
    }

    public List<String> suggestJobs(List<String> skills) {
        Map<String, Integer> freq = new LinkedHashMap<>();
        for (String skill : skills) {
            List<String> jobs = SKILL_TO_JOBS.getOrDefault(skill.toLowerCase(), Collections.emptyList());
            for (String job : jobs) freq.merge(job, 1, Integer::sum);
        }
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<String> getJobTitles() {
        List<String> titles = new ArrayList<>(JOB_SKILLS.keySet());
        Collections.sort(titles);
        return titles;
    }

    public List<String> getRequiredSkills(String jobTitle) {
        return JOB_SKILLS.getOrDefault(jobTitle, Collections.emptyList());
    }

    private List<String> tokenize(String text) {
        if (text == null) return Collections.emptyList();
        return Arrays.stream(text.toLowerCase()
                        .replaceAll("[^a-z0-9\\s.#+/]", " ")
                        .split("\\s+"))
                .filter(t -> t.length() > 2)
                .collect(Collectors.toList());
    }

    private Map<String, Double> buildTfIdfVector(List<String> tokens) {
        Map<String, Long> freq = new HashMap<>();
        for (String t : tokens) freq.merge(t, 1L, Long::sum);
        Map<String, Double> vector = new HashMap<>();
        long total = tokens.size();
        if (total == 0) return vector;
        for (Map.Entry<String, Long> e : freq.entrySet()) {
            double tf = (double) e.getValue() / total;
            double idf = e.getKey().length() <= 3 ? 0.5 : 1.0;
            vector.put(e.getKey(), tf * idf);
        }
        return vector;
    }

    private double magnitude(Map<String, Double> vector) {
        return Math.sqrt(vector.values().stream().mapToDouble(v -> v * v).sum());
    }

    // ── MatchResult DTO ────────────────────────────────────────────
    public static class MatchResult {
        private int matchPercentage;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private List<String> suggestions;
        private List<String> recommendedRoles;

        public MatchResult() {
            matchPercentage = 0;
            matchedSkills = Collections.emptyList();
            missingSkills = Collections.emptyList();
            suggestions = Collections.emptyList();
            recommendedRoles = Collections.emptyList();
        }

        public MatchResult(int pct, List<String> matched, List<String> missing,
                           List<String> suggestions, List<String> recommended) {
            this.matchPercentage = pct;
            this.matchedSkills = matched;
            this.missingSkills = missing;
            this.suggestions = suggestions;
            this.recommendedRoles = recommended;
        }

        public int getMatchPercentage()         { return matchPercentage; }
        public List<String> getMatchedSkills()  { return matchedSkills; }
        public List<String> getMissingSkills()  { return missingSkills; }
        public List<String> getSuggestions()    { return suggestions; }
        public List<String> getRecommendedRoles() { return recommendedRoles; }
    }
}

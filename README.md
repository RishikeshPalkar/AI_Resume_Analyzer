# AI Resume Analyzer & Job Matcher

[![CI](https://github.com/RiskyRexx/AI_Resume_Analyzer/actions/workflows/ci.yml/badge.svg)](https://github.com/RiskyRexx/AI_Resume_Analyzer/actions)
![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)
![React](https://img.shields.io/badge/React-18-blue.svg)
![TypeScript](https://img.shields.io/badge/TypeScript-5.5-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

An enterprise-ready, production-grade full-stack web application that parses resumes (PDF/DOCX), evaluates them against target job descriptions, computes deterministic ATS compatibility scores with weighted breakdowns, and generates AI-driven gap analyses and improvement recommendations.

---

## 🚀 Key Features

- **Document Parsing**: Extracts clean textual content from PDF (Apache PDFBox) and DOCX (Apache POI) files with magic-byte format validation and security checks.
- **Dual AI Engine**: Spring AI 1.0 (OpenAI GPT-4o-mini) integration paired with an intelligent deterministic keyword/NLP fallback engine for offline or quota-limited environments.
- **Deterministic ATS Scoring**:
  - 50% Required Skills Match
  - 25% Experience Relevance
  - 15% Education / Qualifications
  - 10% Preferred / Bonus Skills
- **Security & Authentication**:
  - Stateless JWT access tokens + refresh token rotation with reuse detection.
  - Password hashing with BCrypt (strength 12).
  - Fine-grained role-based access control and isolated user multi-tenancy.
- **Database Migrations**: Version-controlled Flyway migrations for zero-downtime relational schema updates.
- **Modern UI / UX**:
  - Built with React 18, Vite, TypeScript, and Tailwind CSS.
  - Interactive SVG score gauges, categorized skill pills (matched/missing), keyword evidence grids, and paginated match history.
- **Observability**: Correlation ID request tracing (`X-Correlation-Id`), Spring Boot Actuator health probes, structured global error responses (`RFC 7807` style), and OpenAPI 3 / Swagger documentation.

---

## 🛠️ Architecture & Tech Stack

### Backend
- **Java 21** & **Spring Boot 3.4.3**
- **Spring Security** (Stateless JJWT `0.12.6`)
- **Spring Data JPA / Hibernate 6**
- **Spring AI 1.0.0-M6**
- **Flyway** for database versioning
- **MySQL 8** (Production/Dev) & **H2** (In-Memory Testing)
- **Springdoc OpenAPI 2.8.5** (Swagger UI)

### Frontend
- **React 18** with **TypeScript**
- **Vite 6** build tool
- **Tailwind CSS 3** & **Lucide React** icons
- **Axios** with automatic token interceptors & refresh loops
- **Vitest** & React Testing Library

### DevOps & Containers
- Multi-stage **Dockerfiles** for backend and frontend.
- **Docker Compose** orchestration with health checks.
- **GitHub Actions** CI pipeline for automated testing.

---

## 🏁 Quick Start

### Prerequisites
- JDK 21+
- Node.js 20+ & npm
- Docker & Docker Compose (or local MySQL 8)

### 1. Database Setup
Start MySQL using Docker:
```bash
docker run -d --name resume_analyzer_mysql \
  -e MYSQL_ROOT_PASSWORD=root_temporary_dev_password \
  -e MYSQL_DATABASE=resume_analyzer_db \
  -e MYSQL_USER=analyzer_user \
  -e MYSQL_PASSWORD=analyzer_secret \
  -p 3307:3306 mysql:8.0
```

### 2. Backend Setup
```bash
cd backend
mvn clean package -DskipTests
java -jar target/ai-resume-analyzer-1.0.0.jar --server.port=9090
```
- API Base URL: `http://localhost:9090`
- Swagger UI: `http://localhost:9090/swagger-ui.html`
- Actuator Health: `http://localhost:9090/actuator/health`

### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
- Web Application: `http://localhost:5173`

---

## 🧪 Testing

### Backend Unit & Integration Tests
```bash
cd backend
mvn clean test
```

### Frontend Component Tests
```bash
cd frontend
npm test
```

---

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

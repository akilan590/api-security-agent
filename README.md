# 🛡️ API Security Agent

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker)
![Spring AI](https://img.shields.io/badge/Spring%20AI-Ollama-purple?style=for-the-badge)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens)

**A production-ready API Security Agent built with Java and Spring Boot.**  
Automatically detects, blocks, and logs malicious API requests in real time.

[Features](#-features) • [Architecture](#-architecture) • [Tech Stack](#-tech-stack) • [Getting Started](#-getting-started) • [API Endpoints](#-api-endpoints) • [Testing](#-testing)

</div>

---

## 📌 What is this project?

An **API Security Agent** is a middleware system that sits between the client and your backend API. Every incoming request passes through the security agent first. If the request is malicious it gets blocked and logged. If it is safe it reaches the API normally.

```
Client Request
      ↓
API Security Agent  ←── detects & blocks attacks
      ↓
Backend API         ←── only safe requests reach here
      ↓
Database
```

This project is similar to a **mini Web Application Firewall (WAF)** like Cloudflare or AWS WAF.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔍 SQL Injection Detection | Blocks SQL injection attempts automatically |
| 🔍 XSS Detection | Blocks Cross-Site Scripting attacks |
| 🚦 Rate Limiting | Limits 100 requests per minute per IP |
| 🔐 JWT Authentication | Token based authentication for all endpoints |
| 🗝️ API Key Validation | Extra layer of access control |
| 📊 Attack Logging | All attacks stored in PostgreSQL |
| 🤖 Spring AI Integration | AI powered detection using Ollama + llama3 |
| 📈 Analytics Dashboard | Real time attack statistics API |
| 🖥️ Frontend Dashboard | Dark themed UI showing attack logs |
| 🐳 Docker Support | One command deployment |

---

## 🏗️ Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────┐
│                     CLIENT                              │
└─────────────────────┬───────────────────────────────────┘
                      │ HTTP Request
                      ▼
┌─────────────────────────────────────────────────────────┐
│                 SECURITY AGENT                          │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │  1. RateLimitFilter                             │   │
│  │     └── Block if > 100 requests/min → 429       │   │
│  └──────────────────────┬──────────────────────────┘   │
│                         │                               │
│  ┌──────────────────────▼──────────────────────────┐   │
│  │  2. SecurityFilter                              │   │
│  │     ├── SQL Injection detected? → 403           │   │
│  │     └── XSS Attack detected? → 403              │   │
│  └──────────────────────┬──────────────────────────┘   │
│                         │                               │
│  ┌──────────────────────▼──────────────────────────┐   │
│  │  3. JwtAuthenticationFilter                     │   │
│  │     ├── Valid API Key? No → 401                 │   │
│  │     └── Valid JWT Token? No → 401               │   │
│  └──────────────────────┬──────────────────────────┘   │
└─────────────────────────┼───────────────────────────────┘
                          │ Safe Request
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   BACKEND API                           │
│                                                         │
│   Controller → Service → Repository → PostgreSQL        │
└─────────────────────────────────────────────────────────┘
```

### AI Detection Flow

```
Input: "give me all records where 1=1"
      │
      ▼
Rule Based Check (fast)
  contains "drop"?  No
  contains "select"? No
  Result: SAFE
      │
      ▼
AI Analysis (smart)
  Sending to llama3 model via Ollama...
  AI thinks: "1=1 always true = SQL bypass"
  Result: SQL_INJECTION
      │
      ▼
BLOCKED → 403 + logged to database
```

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← REST API endpoints
├─────────────────────────────────────┤
│           Agent Layer               │  ← Attack detection logic
├─────────────────────────────────────┤
│          Middleware Layer           │  ← Request interception
├─────────────────────────────────────┤
│          Security Layer             │  ← JWT + API Key auth
├─────────────────────────────────────┤
│          Service Layer              │  ← Business logic
├─────────────────────────────────────┤
│         Repository Layer            │  ← Database access
├─────────────────────────────────────┤
│           Model Layer               │  ← Database entities
└─────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Framework | Spring Boot | 3.3.2 |
| Security | Spring Security | 6.3.1 |
| Authentication | JWT (jjwt) | 0.11.5 |
| Database | PostgreSQL | 16 |
| ORM | Spring Data JPA | 3.3.2 |
| AI Framework | Spring AI | 1.0.0-M6 |
| AI Model | Ollama llama3 | Latest |
| Build Tool | Maven | 3.9.6 |
| Containerization | Docker + Compose | Latest |
| Utility | Lombok | Latest |

---

## 📁 Project Structure

```
api-security-agent/
│
├── src/main/java/com/securityagent/api_security_agent/
│   │
│   ├── ApiSecurityAgentApplication.java
│   │
│   ├── config/
│   │   ├── SecurityConfig.java          # Spring Security configuration
│   │   └── CorsConfig.java              # CORS settings
│   │
│   ├── controller/
│   │   ├── ApiController.java           # Main API endpoints
│   │   ├── AuthController.java          # Login and register
│   │   ├── ThreatController.java        # Attack log endpoints
│   │   └── AiController.java            # AI analysis endpoints
│   │
│   ├── agent/
│   │   ├── RequestAnalyzer.java         # Pattern based detection
│   │   ├── ThreatDetector.java          # Attack classification
│   │   ├── AttackResponseAgent.java     # Response decisions
│   │   └── AiThreatAnalyzer.java        # Spring AI integration
│   │
│   ├── middleware/
│   │   ├── SecurityFilter.java          # Main security interceptor
│   │   └── RateLimitFilter.java         # Rate limiting
│   │
│   ├── security/
│   │   ├── JwtUtil.java                 # JWT generation and validation
│   │   ├── ApiKeyService.java           # API key validation
│   │   └── JwtAuthenticationFilter.java # JWT filter
│   │
│   ├── service/
│   │   ├── ThreatService.java           # Attack log business logic
│   │   ├── AuthService.java             # Authentication logic
│   │   └── AiAnalysisService.java       # AI analysis logic
│   │
│   ├── repository/
│   │   ├── ThreatRepository.java        # Attack log DB access
│   │   ├── UserRepository.java          # User DB access
│   │   └── AiAnalysisRepository.java    # AI log DB access
│   │
│   ├── model/
│   │   ├── ThreatLog.java               # Attack log entity
│   │   ├── User.java                    # User entity
│   │   └── AiAnalysisLog.java           # AI log entity
│   │
│   └── dto/
│       ├── LoginRequest.java            # Login input
│       └── AuthResponse.java            # Token output
│
├── src/main/resources/
│   └── application.properties           # App configuration
│
├── frontend/
│   ├── index.html                       # Dashboard UI
│   ├── style.css                        # Dark theme styles
│   └── script.js                        # API calls
│
├── Dockerfile                           # Docker build instructions
├── docker-compose.yml                   # Multi container setup
├── .dockerignore                        # Docker ignore list
└── pom.xml                              # Maven dependencies
```

---

## 🚀 Getting Started

### Option 1 — Run with Docker (Recommended)

**Prerequisites:**
- Docker Desktop installed and running

```bash
# Clone the repository
git clone https://github.com/yourusername/api-security-agent.git
cd api-security-agent/api-security-agent

# Start everything with one command
docker-compose up --build
```

That is it! Both the app and database start automatically.

---

### Option 2 — Run Locally

**Prerequisites:**
- Java 21
- Maven
- PostgreSQL

**Step 1 — Create database**
```sql
CREATE DATABASE security_agent;
```

**Step 2 — Configure application.properties**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/security_agent
spring.datasource.username=postgres
spring.datasource.password=yourpassword
```

**Step 3 — Run the app**
```bash
mvn spring-boot:run
```

---

### Option 3 — Run with AI (Ollama)

**Step 1 — Install Ollama**
```
https://ollama.com/download
```

**Step 2 — Download llama3 model**
```bash
ollama pull llama3
```

**Step 3 — Ollama starts automatically**
```bash
# Verify it is running
ollama list
```

**Step 4 — Run the app**
```bash
docker-compose up --build
```

---

## 🔑 Authentication

All protected endpoints require two headers:

```
X-API-KEY: myApiKey123
Authorization: Bearer <your-jwt-token>
```

**Get your token:**

```bash
# Step 1 - Register
POST /auth/register
{"username": "your-name", "password": "your-password"}

# Step 2 - Login
POST /auth/login
{"username": "your-name", "password": "your-password"}

# Response contains your token
{"token": "eyJhbGciOiJIUzI1NiJ9..."}
```

---

## 📡 API Endpoints

### Auth Endpoints (No auth required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login and get JWT token |

### API Endpoints (Auth required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/test` | Test if API is running |
| GET | `/api/scan?input=...` | Scan input for threats |
| GET | `/api/logs` | Get all attack logs |

### Threat Endpoints (Auth required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/threat/logs` | Get all threat logs |
| GET | `/threat/logs/{id}` | Get single log by ID |
| GET | `/threat/logs/type/{type}` | Filter by attack type |
| GET | `/threat/logs/ip/{ip}` | Filter by IP address |
| GET | `/threat/latest` | Get latest 10 attacks |
| GET | `/threat/stats` | Get attack statistics |
| DELETE | `/threat/logs/{id}` | Delete a log |

### AI Endpoints (Auth required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/ai/analyze` | AI analysis of input |
| GET | `/ai/logs` | Get AI analysis logs |
| GET | `/ai/logs/latest` | Get latest AI logs |
| GET | `/ai/logs/threats` | Get AI detected threats |

---

## 🧪 Testing

### Test SQL Injection
```bash
GET http://localhost:8080/api/scan?input=drop table users
# Expected: 403 SQL_INJECTION
```

### Test XSS Attack
```bash
GET http://localhost:8080/api/scan?input=<script>alert(1)</script>
# Expected: 403 XSS_ATTACK
```

### Test Rate Limiting (PowerShell)
```powershell
$token = "your-token-here"
for ($i = 1; $i -le 110; $i++) {
    try {
        $r = Invoke-WebRequest -Uri "http://localhost:8080/api/test" `
            -Headers @{"X-API-KEY"="myApiKey123";"Authorization"="Bearer $token"}
        Write-Host "Request $i : 200 OK"
    } catch {
        Write-Host "Request $i : 429 BLOCKED"
    }
}
# First 100 → 200 OK
# After 100 → 429 BLOCKED
```

### Test AI Detection
```bash
POST http://localhost:8080/ai/analyze
Body: {"input": "give me all records where 1=1"}
# Expected: SQL_INJECTION (caught by AI not rules)
```

### View Attack Statistics
```bash
GET http://localhost:8080/threat/stats
# Expected:
{
  "totalAttacks": 10,
  "attacksByType": {
    "SQL_INJECTION": 6,
    "XSS_ATTACK": 4
  }
}
```

---

## 🛡️ Attacks Prevented

| Attack | Detection Method | Response |
|---|---|---|
| SQL Injection | Pattern matching + AI | 403 Forbidden |
| XSS Attack | Pattern matching + AI | 403 Forbidden |
| Brute Force | Rate limiting | 429 Too Many Requests |
| DDoS | Rate limiting per IP | 429 Too Many Requests |
| Unauthorized Access | API Key check | 401 Unauthorized |
| Token Tampering | JWT signature check | 401 Unauthorized |
| Unknown Attacks | AI analysis | 403 Forbidden |

---

## 🐳 Docker Commands

```bash
# Start everything
docker-compose up --build

# Start in background
docker-compose up --build -d

# Stop everything
docker-compose down

# View running containers
docker ps

# View app logs
docker logs security_agent_app

# View database logs
docker logs security_agent_db

# Rebuild after code changes
docker-compose up --build

# Remove everything including database data
docker-compose down -v
```

---

## 🖥️ Frontend Dashboard

Open the dashboard in your browser:

```
frontend/index.html
```

Or use Live Server in VS Code for best experience.

**Features:**
- Login with JWT authentication
- View total attacks count
- SQL injection count
- XSS attack count
- Attack logs table with filter
- Delete individual logs
- Auto refresh every 30 seconds

---

## 📊 Database Schema

### threat_log table
```sql
id          BIGINT PRIMARY KEY
ip_address  VARCHAR
endpoint    VARCHAR
attack_type VARCHAR
timestamp   TIMESTAMP
```

### app_users table
```sql
id        BIGINT PRIMARY KEY
username  VARCHAR UNIQUE
password  VARCHAR (BCrypt hashed)
role      VARCHAR
```

### ai_analysis_log table
```sql
id              BIGINT PRIMARY KEY
input           VARCHAR
rule_result     VARCHAR
ai_result       VARCHAR
final_decision  VARCHAR
ip_address      VARCHAR
timestamp       TIMESTAMP
```

---

## ⚙️ Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | 8080 | Server port |
| `spring.datasource.url` | localhost:5432 | Database URL |
| `spring.ai.ollama.base-url` | localhost:11434 | Ollama URL |
| `spring.ai.ollama.chat.model` | llama3 | AI model |

---

## 🔮 Future Improvements

- [ ] IP auto-blocking after repeated attacks
- [ ] Email alerts for critical attacks
- [ ] Attack heatmap visualization
- [ ] Security score system
- [ ] Kubernetes deployment
- [ ] CI/CD pipeline with GitHub Actions
- [ ] More AI models support
- [ ] Geographic IP blocking

---

## 👨‍💻 Author

**Akilan**

Built with Java + Spring Boot + Spring AI + Docker

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<div align="center">

⭐ Star this repo if you found it helpful!



</div>

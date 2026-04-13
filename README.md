# 🚀 Project Analyzer

**Project Analyzer** is a **Spring Boot–based AI application** that analyzes repository `README.md` files and generates **structured, interview-friendly project insights**.

It helps **students and developers** quickly understand, revise, and confidently explain projects without diving deep into the entire codebase.

---

## 🎯 Motivation

Students and developers often build projects for:

* Learning
* Coursework
* Resume building
* Professional growth

Over time, remembering:

* Design decisions
* Architecture
* Features

becomes difficult — especially before interviews.

Manually revisiting code is:

* ⏱️ Time-consuming
* 😓 Inefficient

### ✅ Solution

This project solves that by:

* Using `README.md` as the **single source of truth**
* Generating **structured explanations using AI**
* Ensuring **accurate, non-hallucinated output**

---

## 🧠 How It Works

```
User Input (Repo URL)
        ↓
Controller
        ↓
Repository Service (GitHub / GitLab)
        ↓
Fetch README.md
        ↓
LLM (LLaMA 3.1 via Groq)
        ↓
Structured Analysis
        ↓
User Interface
```

### 🔄 Flow Explanation

1. User enters a repository URL
2. Backend detects platform (**GitHub / GitLab**)
3. README is fetched using respective APIs
4. Validation is performed:

   * URL correctness
   * Repository accessibility
   * README presence
   * Documentation quality
5. README is sent to **LLM for analysis**
6. Structured output is displayed

---

## ✨ Key Features

### 🔍 Core Functionality

* README-based analysis (no assumptions)
* Multi-platform support:

  * ✅ GitHub
  * ✅ GitLab
  * 🔜 Bitbucket (planned)

---

### ⚠️ Smart Validation

* Invalid repository URL detection
* Missing README detection
* Weak documentation detection

---

### 🧩 Structured Output

* Project Overview
* Key Features
* Tech Stack
* Architecture / Flow
* Interview Explanation
* Improvements
* README Quality Score
* Missing Documentation Sections

---

### 🧠 Responsible AI Usage

* No hallucination
* Explicit handling of missing data
* Clear and honest responses

---

### 🎨 User Experience

* Markdown rendering
* Dark mode 🌙
* Copy-to-clipboard 📋
* Loading indicators ⏳

---

## 🎓 Who Is This For?

### 👨‍🎓 Students

* Revise projects quickly
* Prepare for interviews & viva
* Learn structured explanation

---

### 👨‍💻 Developers

* Understand unfamiliar repositories
* Evaluate documentation quality
* Get quick technical summaries

---

## 🛠️ Tech Stack

### Backend

* **Java** – Core programming language
* **Spring Boot** – Backend framework for REST APIs
* **REST APIs** – Communication layer
* **Maven** – Build and dependency management

---

### Frontend

* **HTML** – Structure
* **CSS** – Styling
* **JavaScript** – Interactivity
* **Marked.js** – Markdown rendering

---

### AI & APIs

* **Groq API** – LLM inference platform
* **LLaMA 3.1 (8B)** – AI model for analysis
* **GitHub REST API** – Fetch README
* **GitLab API** – Fetch README using project ID

---

## 🏗️ Architecture

### 🔹 Design Pattern

The application follows a **modular service-based architecture**:

```
Controller
   ↓
RepositoryService (Interface)
   ↓
 ├── GitHubService
 ├── GitLabService
 └── (Future: BitbucketService)
   ↓
LLMService
```

### 🔹 Key Design Decisions

* **Abstraction Layer** using `RepositoryService`
* **Platform-independent logic**
* Easy extensibility for new platforms
* Clean separation of concerns

---

## ⚙️ Installation & Setup

### 🔹 Prerequisites

* Java 17+
* Maven
* Git

---

### 🔹 Clone the Repository

```
git clone https://github.com/your-username/project-analyzer.git
cd project-analyzer
```

---

### 🔹 Configure Environment Variables

#### Windows (PowerShell):

```
setx GROQ_API_KEY "your_api_key"
```

#### Linux / Mac:

```
export GROQ_API_KEY=your_api_key
```

---

### 🔹 Application Configuration

`application.properties`

```
groq.api.key=${GROQ_API_KEY}
```

---

### 🔹 Run the Application

```
mvn spring-boot:run
```

---

### 🔹 Access the App

```
http://localhost:8080
```

---

## 📌 Usage

1. Open the web interface
2. Enter repository URL:

```
https://github.com/username/repo
OR
https://gitlab.com/username/repo
```

3. Click **Analyze**
4. View structured output

---

## ⚠️ Important Notes

* Only **public repositories** are supported
* `README.md` must be present
* Better documentation → better analysis

---

## 🔮 Future Enhancements

* Bitbucket integration
* Private repository support (authentication)
* Caching for faster responses
* Advanced README scoring
* Chatbot for interactive Q&A
* Deployment monitoring

---

## 🚀 Deployment

* Designed for deployment on **Render**
* Uses **environment variables for secrets**
* Supports containerization via Docker

---

## 🔐 Security

* API keys are **NOT stored in code**
* Uses **environment variables**
* Prevents secret exposure

---

## 📄 License

This project is for:

* Learning
* Demonstration
* Portfolio use

---

## 🙌 Final Note

This project focuses on:

* Clean architecture
* User clarity
* Responsible AI usage
* Real-world engineering practices

It is designed to help **students succeed in interviews** while also being useful for developers.

---

## ⭐ Support

If you found this helpful:

* ⭐ Star the repository
* 🍴 Fork it
* 🛠️ Contribute

---

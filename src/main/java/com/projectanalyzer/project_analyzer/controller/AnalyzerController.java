package com.projectanalyzer.project_analyzer.controller;

import com.projectanalyzer.project_analyzer.service.GitHubService;
import com.projectanalyzer.project_analyzer.service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AnalyzerController {

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private LLMService llmService;

    @PostMapping("/analyze")
    public String analyzeProject(@RequestBody String repoUrl) {

        // Step 1: Fetch README (validation + normalization handled inside service)
        String readme = gitHubService.fetchReadme(repoUrl);

        // ❌ Invalid GitHub URL
        if ("INVALID_URL".equals(readme)) {
            return """
❌ **Invalid GitHub Repository URL**

The URL provided is **not a valid GitHub repository link**.

✅ Please use the following format:
https://github.com/username/repository

📌 Example:
https://github.com/spring-projects/spring-boot
""";
        }

        // ❌ README missing or repository inaccessible
        if ("README_NOT_FOUND".equals(readme)) {
            return """
❌ **README.md Not Found**

The provided GitHub repository does **not contain a readable README.md**
or the repository could not be accessed.

⚠️ Project Analyzer relies heavily on README.md for accurate analysis.

✅ Please ensure:
- The repository exists
- It is public
- A README.md file is present in the root directory
""";
        }

        // ⚠️ README exists but is weak
        if ("WEAK_README".equals(readme)) {
            return """
⚠️ **Weak README Detected**

A README.md file was found, but it appears to be **too short or insufficiently detailed**.

⚠️ The analysis may be **generic or partially inaccurate**.

💡 For best results, consider adding:
- Clear project overview
- Tech stack used
- Architecture or workflow
- Features
- Possible improvements
""";
        }

        // ✅ Step 2: Safe to analyze using LLM
        return llmService.analyzeProject(readme);
    }
}

package com.projectanalyzer.project_analyzer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GitLabService implements RepositoryService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String fetchReadme(String repoUrl) {
        try {
            // Step 1: Extract repo name
            String cleanUrl = repoUrl.replace("https://gitlab.com/", "");
            String[] parts = cleanUrl.split("/");

            if (parts.length < 2) return "INVALID_URL";

            String repoName = parts[1];

            // Step 2: Search project
            String searchUrl = "https://gitlab.com/api/v4/projects?search=" + repoName;

            List<Map<String, Object>> projects = restTemplate.getForObject(searchUrl, List.class);

            if (projects == null || projects.isEmpty()) return "README_NOT_FOUND";

            // Step 3: Find exact match
            Integer projectId = null;

            for (Map<String, Object> project : projects) {
                String path = (String) project.get("path");

                if (repoName.equalsIgnoreCase(path)) {
                    projectId = (Integer) project.get("id");
                    break;
                }
            }

            if (projectId == null) return "README_NOT_FOUND";

            // Step 4: Try fetching README
            String[] branches = {"main", "master"};

            for (String branch : branches) {
                try {
                    String apiUrl = "https://gitlab.com/api/v4/projects/"
                            + projectId
                            + "/repository/files/README.md/raw?ref=" + branch;

                    return restTemplate.getForObject(apiUrl, String.class);

                } catch (Exception ignored) {}
            }

            return "README_NOT_FOUND";

        } catch (Exception e) {
            return "README_NOT_FOUND";
        }
    }
}
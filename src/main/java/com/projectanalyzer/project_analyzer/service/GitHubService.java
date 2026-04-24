package com.projectanalyzer.project_analyzer.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService implements RepositoryService {

    private static final int MIN_README_LENGTH = 200;

    @Value("${github.token:}")
    private String githubToken;

    private final RestTemplate restTemplate = new RestTemplate();

    // ===================== CHECK REPO EXISTS =====================

    private boolean repoExists(String owner, String repo) {
        try {
            String url = "https://api.github.com/repos/" + owner + "/" + repo;

            HttpEntity<String> entity = new HttpEntity<>(buildHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();

        } catch (HttpClientErrorException e) {
            // 🔥 Only 404 = invalid repo
            return e.getStatusCode() != HttpStatus.NOT_FOUND;
        }
    }

    // ===================== FETCH README =====================

    @Override
    public String fetchReadme(String repoUrl) {
        try {
            if (repoUrl == null || repoUrl.trim().isEmpty()) {
                return "INVALID_URL";
            }

            repoUrl = normalizeUrl(repoUrl);

            if (!repoUrl.startsWith("https://github.com/")) {
                return "INVALID_URL";
            }

            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) return "INVALID_URL";

            String owner = parts[0];
            String repo = parts[1];

            if (!repoExists(owner, repo)) {
                return "INVALID_URL";
            }

            // 🔥 PRIMARY: /readme endpoint
            try {
                String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/readme";

                HttpHeaders headers = buildHeaders();
                headers.set("Accept", "application/vnd.github.v3+json");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        apiUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                JSONObject json = new JSONObject(response.getBody());
                String encodedContent = json.getString("content");

                String decoded = new String(
                        Base64.getDecoder().decode(encodedContent.replaceAll("\\s", ""))
                ).trim();

                if (decoded.length() < MIN_README_LENGTH) {
                    return "WEAK_README";
                }

                return decoded;

            } catch (Exception ignored) {
                // 🔁 fallback below
            }

            // 🔥 FALLBACK: manual file fetch
            String[] branches = {"main", "master"};
            String[] files = {"README.md", "readme.md", "Readme.md"};

            for (String branch : branches) {
                for (String file : files) {
                    try {
                        String url = "https://api.github.com/repos/"
                                + owner + "/" + repo
                                + "/contents/" + file + "?ref=" + branch;

                        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());

                        ResponseEntity<Map> response = restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                entity,
                                Map.class
                        );

                        if (response.getBody() == null) continue;

                        Object contentObj = response.getBody().get("content");
                        if (contentObj == null) continue;

                        String content = (String) contentObj;

                        String decoded = new String(
                                Base64.getDecoder().decode(content.replaceAll("\\s", ""))
                        ).trim();

                        return decoded;

                    } catch (Exception ignored) {}
                }
            }

            return "README_NOT_FOUND";

        } catch (Exception e) {
            return "README_NOT_FOUND";
        }
    }

    // ===================== FETCH STRUCTURE =====================

    @Override
    public String fetchRepoStructure(String repoUrl) {
        try {
            repoUrl = normalizeUrl(repoUrl);

            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) return "INVALID_URL";

            String owner = parts[0];
            String repo = parts[1];

            if (!repoExists(owner, repo)) {
                return "INVALID_URL";
            }

            String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/contents";

            HttpEntity<String> entity = new HttpEntity<>(buildHeaders());

            ResponseEntity<List> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            List<Map<String, Object>> files = response.getBody();
            if (files == null) return "No repository structure available.";

            StringBuilder structure = new StringBuilder();

            for (Map<String, Object> file : files) {
                String name = (String) file.get("name");
                String type = (String) file.get("type");

                structure.append("dir".equals(type) ? "[DIR] " : "[FILE] ");
                structure.append(name).append("\n");
            }

            return structure.toString();

        } catch (Exception e) {
            return "Could not fetch repository structure.";
        }
    }

    // ===================== FETCH KEY FILES =====================

    public String fetchKeyFiles(String repoUrl) {
        try {
            repoUrl = normalizeUrl(repoUrl);

            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) return "No key files available.";

            String owner = parts[0];
            String repo = parts[1];

            if (!repoExists(owner, repo)) {
                return "No key files available.";
            }

            String apiBase = "https://api.github.com/repos/" + owner + "/" + repo;

            String[] importantFiles = {
                    "pom.xml",
                    "package.json",
                    "application.properties",
                    "Dockerfile"
            };

            HttpEntity<String> entity = new HttpEntity<>(buildHeaders());

            StringBuilder result = new StringBuilder();

            for (String fileName : importantFiles) {
                try {
                    String url = apiBase + "/contents/" + fileName;

                    ResponseEntity<Map> response = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            Map.class
                    );

                    if (response.getBody() == null) continue;

                    Object contentObj = response.getBody().get("content");
                    if (contentObj == null) continue;

                    String content = (String) contentObj;

                    String decoded = new String(
                            Base64.getDecoder().decode(content.replaceAll("\\s", ""))
                    );

                    decoded = decoded.substring(0, Math.min(decoded.length(), 1500));

                    result.append("=== ").append(fileName).append(" ===\n");
                    result.append(decoded).append("\n\n");

                } catch (Exception ignored) {}
            }

            return result.toString();

        } catch (Exception e) {
            return "Could not fetch key files.";
        }
    }

    // ===================== HELPERS =====================

    private String normalizeUrl(String repoUrl) {
        repoUrl = repoUrl.trim();
        if (repoUrl.endsWith("/")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 1);
        }
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 4);
        }
        return repoUrl;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Project-Analyzer-App");

        if (githubToken != null && !githubToken.isBlank()) {
            headers.setBearerAuth(githubToken);
        }

        return headers;
    }
}
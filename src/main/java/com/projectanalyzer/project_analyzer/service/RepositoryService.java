package com.projectanalyzer.project_analyzer.service;

public interface RepositoryService {
    
    String fetchReadme(String repoUrl);

    String fetchRepoStructure(String repoUrl);
}
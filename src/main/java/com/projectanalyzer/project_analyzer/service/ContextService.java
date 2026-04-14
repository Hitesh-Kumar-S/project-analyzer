package com.projectanalyzer.project_analyzer.service;

import org.springframework.stereotype.Service;

@Service
public class ContextService {

    private String currentReadme;

    public void setReadme(String readme) {
        this.currentReadme = readme;
    }

    public String getReadme() {
        return currentReadme;
    }

    public boolean hasContext() {
        return currentReadme != null && !currentReadme.isEmpty();
    }

    public void clear() {
        this.currentReadme = null;
    }
}
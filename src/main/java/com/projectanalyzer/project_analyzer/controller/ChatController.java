package com.projectanalyzer.project_analyzer.controller;

import com.projectanalyzer.project_analyzer.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public String chat(@RequestBody String question) {

        // Clean input
        if (question == null || question.trim().isEmpty()) {
            return """
⚠️ **Empty Question**

Please enter a valid question about the project.
""";
        }

        return chatService.chat(question.trim());
    }
}
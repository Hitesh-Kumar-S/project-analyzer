package com.projectanalyzer.project_analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private ContextService contextService;

    @Autowired
    private GroqLLMService groqService;

    @Autowired
    private OpenRouterLLMService openRouterService;

    public String chat(String question, boolean strictMode) {

        if (!contextService.hasContext()) {
            return "❌ Please analyze a repository first.";
        }

        String context = contextService.buildContext();

        String prompt = strictMode
                ? buildStrictPrompt(context, question)
                : buildSmartPrompt(context, question);

        if (strictMode) {
    return groqService.generateResponse(prompt);
} else {
    try {
        return openRouterService.generateResponse(prompt);
    } catch (Exception e) {
        // 🔥 fallback to Groq
        try {
            return groqService.generateResponse(prompt);
        } catch (Exception ex) {
            return "⚠️ I'm experiencing high traffic. Please try again in a few moments.";
        }
    }
}
    }

    // 🔒 STRICT MODE
    private String buildStrictPrompt(String context, String question) {
        return """
You are an AI assistant.

Answer ONLY using the project README content below.
Do NOT use any external knowledge.
Do NOT guess or assume anything.

If the answer is not present in the README, respond exactly with:
"This is not mentioned in the project README."

README:
%s

Question:
%s
""".formatted(context, question);
    }

    // 🧠 SMART MODE (IMPROVED 🔥)
    private String buildSmartPrompt(String context, String question) {
        return """
You are an expert software engineer and AI assistant.

You are helping a user understand a project and answer technical questions.

You are given project context (README + structure), but you are NOT limited to it.

You can:
- Answer questions about the project
- Explain technologies
- Suggest improvements
- Compare tech stacks
- Answer general programming questions

IMPORTANT:
- Use project context when relevant
- If context is missing, use your own knowledge
- Do NOT say "according to the README"
- Do NOT restrict yourself only to the context
- Give clear, confident, and direct answers

Tone:
- Professional
- Concise
- Interview-ready

Context:
%s

Question:
%s
""".formatted(context, question);
    }
}
package com.projectanalyzer.project_analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import com.projectanalyzer.project_analyzer.service.LLMService;

@Service
public class ChatService {

    @Autowired
    private ContextService contextService;

    @Autowired
    private LLMService llmService;

    public String chat(String userQuestion) {

        // ❌ No context available
        if (!contextService.hasContext()) {
            return """
❌ **No Project Context Found**

Please analyze a repository first before asking questions.

💡 Paste a repository URL and click **Analyze**, then try asking your question again.
""";
        }

        String readme = contextService.getReadme();

        // 🧠 Build chatbot prompt
        String prompt = """
You are a **strict and precise software engineering assistant**.

Your task is to answer questions about a project using ONLY the provided project context.

---

❗ STRICT RULES (VERY IMPORTANT)

- Answer ONLY using the given project context.
- Do NOT assume, infer, or guess anything.
- Do NOT introduce external knowledge.
- If the answer is not clearly present, say exactly:
  "This is not mentioned in the project README."
- Do NOT add unrelated explanations.
-If the question is outside the scope of the project, respond:
  "This question is outside the scope of the project documentation."

---

🎯 ACCURACY RULES

- If the question is about tools (e.g., Maven, Spring Boot):
  - Only explain them in the context of THIS project.
  - Do NOT give generic textbook definitions unless they are clearly relevant.

- If something is partially mentioned:
  - Answer ONLY the known part.
  - Clearly state what is missing.

---

💬 RESPONSE STYLE

- Keep answers **concise and clear**
- Use bullet points where helpful
- Highlight key terms in **bold**
- Avoid long paragraphs

---

📦 PROJECT CONTEXT:
""" + readme + """

---

❓ USER QUESTION:
""" + userQuestion + """

---

✅ FINAL ANSWER:

Answer:
""";

        return llmService.callLLM(prompt);
    }
}
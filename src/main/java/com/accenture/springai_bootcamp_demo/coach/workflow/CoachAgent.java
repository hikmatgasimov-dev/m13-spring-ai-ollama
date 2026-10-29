package com.accenture.springai_bootcamp_demo.coach.workflow;

import com.accenture.springai_bootcamp_demo.coach.progress.TopicProgress;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Agent 3: turns the grade and the saved progress into short, encouraging feedback.
 */
@Component
public class CoachAgent {

    private static final String SYSTEM_PROMPT = """
            You are Study Coach, a friendly mentor for Java bootcamp trainees.
            Write feedback in at most 3 sentences: say what was good or missing, then suggest one next step.
            Do not change the verdict. Use only the numbers you are given; never invent any.
            """;

    private final ChatClient chatClient;

    public CoachAgent(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem(SYSTEM_PROMPT).build();
    }

    public String feedback(String question, String answer, Grade grade, TopicProgress progress) {
        String reply = chatClient.prompt()
                .user("""
                        Question: %s
                        Trainee's answer: %s
                        Verdict: %s (%s)
                        Progress on "%s": %d attempts, average %d%%, best %d%%
                        """.formatted(question, answer, grade.verdict().name().toLowerCase(), grade.reason(),
                        progress.topic(), progress.attempts(), progress.averagePercent(), progress.bestPercent()))
                .call()
                .content();
        return StringUtils.hasText(reply) ? reply.trim() : grade.reason();
    }
}
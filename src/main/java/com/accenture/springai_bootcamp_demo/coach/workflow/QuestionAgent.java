package com.accenture.springai_bootcamp_demo.coach.workflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Agent 1: writes one exam question from the study notes.
 */
@Slf4j
@Component
public class QuestionAgent {

    static final int MAX_ATTEMPTS = 3;
    static final String NO_NOTES = "NO_NOTES";

    private static final String SYSTEM_PROMPT = """
            You write ONE exam question for Java bootcamp trainees, based ONLY on the study notes in the message.
            The question must be answerable from the notes in one to three sentences.
            Reply with the question only, nothing else.
            If the notes do not cover the topic, reply exactly: NO_NOTES
            """;

    private final ChatClient chatClient;

    public QuestionAgent(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem(SYSTEM_PROMPT).build();
    }

    public String ask(String topic, String notes) {
        String lastProblem = "no attempt made";
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String reply = chatClient.prompt()
                    .user("Topic: %s\n\nStudy notes:\n%s".formatted(topic, notes))
                    .call()
                    .content();
            if (reply != null && reply.contains(NO_NOTES)) {
                throw new TopicNotCoveredException("The study notes do not cover the topic '%s'".formatted(topic));
            }
            String problem = findProblem(reply);
            if (problem == null) {
                return reply.trim();
            }
            lastProblem = problem;
            log.warn("Question attempt {}/{} was not usable: {}", attempt, MAX_ATTEMPTS, problem);
        }
        throw new AgentOutputException(
                "The question agent did not produce a usable question after %d attempts (%s)"
                        .formatted(MAX_ATTEMPTS, lastProblem));
    }

    /** Returns what is wrong with the question, or null if it is fine. */
    static String findProblem(String question) {
        if (!StringUtils.hasText(question)) {
            return "empty reply";
        }
        String trimmed = question.trim();
        if (!trimmed.endsWith("?")) {
            return "reply is not a question";
        }
        if (trimmed.length() > 300) {
            return "question is too long";
        }
        return null;
    }
}
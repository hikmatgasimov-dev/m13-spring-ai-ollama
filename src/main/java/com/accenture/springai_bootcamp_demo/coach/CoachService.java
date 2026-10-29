package com.accenture.springai_bootcamp_demo.coach;

import com.accenture.springai_bootcamp_demo.client.OpenRouterException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The study-coach use case. Each method gives the (small, local) model one narrow job and checks what comes back:
 * "bounded model + validation + retry".
 */
@Slf4j
@Service
public class CoachService {

    static final int MAX_ATTEMPTS = 3;

    private static final String COACH_SYSTEM_PROMPT = """
            You are a patient Java and Spring study coach for bootcamp trainees.
            Be accurate and concise. If you are not sure about something, say so instead of guessing.
            """;

    private final ChatClient chatClient;

    public CoachService(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem(COACH_SYSTEM_PROMPT).build();
    }

    public ExplainResponse explain(ExplainRequest request) {
        String explanation = ask("""
                Explain "%s" to a %s Java developer in at most 4 sentences. Give one short example if it helps.
                """.formatted(request.topic(), request.level()));
        return new ExplainResponse(request.topic(), request.level(), explanation);
    }

    public QuizResponse generateQuiz(QuizRequest request) {
        String prompt = """
                Create a %s-level multiple-choice quiz about "%s" for Java developers.
                Write exactly %d questions. Each question has exactly 4 DIFFERENT, plausible options,
                and exactly one of them is correct. The three wrong options must be clearly wrong to someone who knows the topic.
                "correctIndex" is the 0-based position of the correct option.
                "explanation" must be one non-empty sentence that says why the correct option is right.
                Set "topic" to "%s".
                """.formatted(request.difficulty(), request.topic(), request.questionCount(), request.topic());

        String lastProblem = "no attempt made";
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                QuizResponse quiz = chatClient.prompt().user(prompt).call().entity(QuizResponse.class);
                String problem = findProblem(quiz, request.questionCount());
                if (problem == null) {
                    return quiz;
                }
                lastProblem = problem;
            } catch (RuntimeException ex) {
                // includes unreadable JSON from the model as well as connection problems
                lastProblem = ex.getMessage();
            }
            log.warn("Quiz attempt {}/{} was not usable: {}", attempt, MAX_ATTEMPTS, lastProblem);
        }
        throw new QuizGenerationException(
                "The model did not produce a valid quiz after %d attempts (%s)".formatted(MAX_ATTEMPTS, lastProblem));
    }

    /** Returns what is wrong with the quiz, or null if it is fine. */
    static String findProblem(QuizResponse quiz, int expectedQuestions) {
        if (quiz == null || quiz.questions() == null || quiz.questions().isEmpty()) {
            return "no questions";
        }
        if (quiz.questions().size() != expectedQuestions) {
            return "expected %d questions but got %d".formatted(expectedQuestions, quiz.questions().size());
        }
        for (QuizQuestion question : quiz.questions()) {
            if (!StringUtils.hasText(question.question())) {
                return "a question has no text";
            }
            List<String> options = question.options();
            if (options == null || options.size() != 4 || options.stream().anyMatch(o -> !StringUtils.hasText(o))) {
                return "a question does not have exactly 4 non-empty options";
            }
            if (question.correctIndex() < 0 || question.correctIndex() >= options.size()) {
                return "correctIndex is outside the options";
            }

            if (!StringUtils.hasText(question.explanation())) {
                return "a question has no explanation";
            }

        }
        return null;
    }

    private String ask(String prompt) {
        try {
            String reply = chatClient.prompt().user(prompt).call().content();
            if (!StringUtils.hasText(reply)) {
                throw new OpenRouterException("The model returned an empty response");
            }
            return reply.trim();
        } catch (OpenRouterException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Model request failed", ex);
            throw new OpenRouterException("Failed to reach the model: " + ex.getMessage(), ex);
        }
    }
}
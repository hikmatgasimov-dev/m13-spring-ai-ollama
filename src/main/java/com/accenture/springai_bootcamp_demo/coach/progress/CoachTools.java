package com.accenture.springai_bootcamp_demo.coach.progress;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@AllArgsConstructor
public class CoachTools {

    private final ProgressService progressService;

    @Tool(description = "Save the result of a quiz the trainee has finished. Call it only when the trainee "
            + "states a topic and a score.")
    public String saveQuizScore(
            @ToolParam(description = "Topic of the quiz, for example 'jpa' or 'spring beans'") String topic,
            @ToolParam(description = "Number of correct answers") int score,
            @ToolParam(description = "Number of questions in the quiz") int total) {
        log.info("Tool saveQuizScore(topic={}, score={}, total={})", topic, score, total);
        try {
            QuizScore saved = progressService.save(topic, score, total);
            return "Saved: %s %d/%d".formatted(saved.getTopic(), saved.getScore(), saved.getTotal());
        } catch (IllegalArgumentException ex) {
            return "Not saved: " + ex.getMessage();
        }
    }

    @Tool(description = "Get the progress of the trainee for every topic: attempts, average and best result in percent.")
    public String getProgress() {
        log.info("Tool getProgress()");
        List<TopicProgress> progress = progressService.progress();
        if (progress.isEmpty()) {
            return "No quiz results saved yet.";
        }
        return progress.stream()
                .map(p -> "%s: %d attempts, average %d%%, best %d%%"
                        .formatted(p.topic(), p.attempts(), p.averagePercent(), p.bestPercent()))
                .collect(Collectors.joining("\n"));
    }

    @Tool(description = "Find the topic with the lowest average result, so the trainee knows what to study next.")
    public String getWeakestTopic() {
        log.info("Tool getWeakestTopic()");
        return progressService.weakestTopic()
                .map(p -> "Weakest topic: %s (average %d%%)".formatted(p.topic(), p.averagePercent()))
                .orElse("No quiz results saved yet.");
    }
}
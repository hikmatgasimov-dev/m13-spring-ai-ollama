package com.accenture.springai_bootcamp_demo.coach.progress;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Saves quiz results and calculates progress. The numbers are always calculated here in Java,
 * never by the model, so they cannot be wrong or invented.
 */
@Service
@AllArgsConstructor
public class ProgressService {

    private final QuizScoreRepository repository;

    public QuizScore save(String topic, int score, int total) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("topic must not be blank");
        }
        if (total < 1) {
            throw new IllegalArgumentException("total must be at least 1");
        }
        if (score < 0 || score > total) {
            throw new IllegalArgumentException("score must be between 0 and " + total);
        }
        return repository.save(QuizScore.create(topic.trim().toLowerCase(), score, total));
    }

    public List<TopicProgress> progress() {
        Map<String, List<QuizScore>> byTopic = repository.findAll().stream()
                .collect(Collectors.groupingBy(QuizScore::getTopic, TreeMap::new, Collectors.toList()));

        return byTopic.entrySet().stream()
                .map(entry -> toProgress(entry.getKey(), entry.getValue()))
                .toList();
    }

    public Optional<TopicProgress> weakestTopic() {
        return progress().stream().min(Comparator.comparingInt(TopicProgress::averagePercent));
    }

    private static TopicProgress toProgress(String topic, List<QuizScore> scores) {
        int average = (int) Math.round(scores.stream().mapToInt(ProgressService::percent).average().orElse(0));
        int best = scores.stream().mapToInt(ProgressService::percent).max().orElse(0);
        return new TopicProgress(topic, scores.size(), average, best);
    }

    private static int percent(QuizScore quizScore) {
        return Math.round(quizScore.getScore() * 100f / quizScore.getTotal());
    }
}
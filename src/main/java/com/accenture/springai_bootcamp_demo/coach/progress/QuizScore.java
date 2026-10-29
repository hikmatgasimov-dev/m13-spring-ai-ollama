package com.accenture.springai_bootcamp_demo.coach.progress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * One finished quiz: how many questions were answered correctly out of how many.
 */
@Entity
@Table(name = "quiz_scores")
@Getter
@NoArgsConstructor
public class QuizScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int total;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public static QuizScore create(String topic, int score, int total) {
        QuizScore quizScore = new QuizScore();
        quizScore.topic = topic;
        quizScore.score = score;
        quizScore.total = total;
        quizScore.createdAt = Instant.now();
        return quizScore;
    }
}
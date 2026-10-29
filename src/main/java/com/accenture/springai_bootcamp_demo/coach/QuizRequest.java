


package com.accenture.springai_bootcamp_demo.coach;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for generating a multiple-choice quiz.
 */
public record QuizRequest(
        @NotBlank(message = "topic must not be blank")
        @Size(max = 200, message = "topic must not exceed 200 characters")
        String topic,
        @Min(value = 1, message = "questionCount must be at least 1")
        @Max(value = 10, message = "questionCount must be at most 10")
        Integer questionCount,
        String difficulty) {

    public QuizRequest {
        if (questionCount == null) {
            questionCount = 3;
        }
        if (difficulty == null || difficulty.isBlank()) {
            difficulty = "beginner";
        }
    }
}
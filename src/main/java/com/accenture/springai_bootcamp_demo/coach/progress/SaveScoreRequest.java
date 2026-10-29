package com.accenture.springai_bootcamp_demo.coach.progress;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SaveScoreRequest(
        @NotBlank(message = "topic must not be blank") String topic,
        @Min(value = 0, message = "score must not be negative") int score,
        @Min(value = 1, message = "total must be at least 1") int total) {
}
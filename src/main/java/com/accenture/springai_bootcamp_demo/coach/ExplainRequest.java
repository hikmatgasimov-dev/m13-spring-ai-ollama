package com.accenture.springai_bootcamp_demo.coach;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for a short explanation of a topic.
 */
public record ExplainRequest(
        @NotBlank(message = "topic must not be blank")
        @Size(max = 200, message = "topic must not exceed 200 characters")
        String topic,
        String level) {

    public ExplainRequest {
        if (level == null || level.isBlank()) {
            level = "beginner";
        }
    }
}
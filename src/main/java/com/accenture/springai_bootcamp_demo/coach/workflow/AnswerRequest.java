package com.accenture.springai_bootcamp_demo.coach.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerRequest(
        @NotBlank(message = "topic must not be blank")
        @Size(max = 100, message = "topic must not exceed 100 characters")
        String topic,
        @NotBlank(message = "question must not be blank")
        @Size(max = 500, message = "question must not exceed 500 characters")
        String question,
        @NotBlank(message = "answer must not be blank")
        @Size(max = 1000, message = "answer must not exceed 1000 characters")
        String answer) {
}
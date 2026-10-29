package com.accenture.springai_bootcamp_demo.coach.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StartSessionRequest(
        @NotBlank(message = "topic must not be blank")
        @Size(max = 100, message = "topic must not exceed 100 characters")
        String topic) {
}
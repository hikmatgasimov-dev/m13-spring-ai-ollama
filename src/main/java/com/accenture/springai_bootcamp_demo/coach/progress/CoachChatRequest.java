package com.accenture.springai_bootcamp_demo.coach.progress;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CoachChatRequest(
        @NotBlank(message = "message must not be blank")
        @Size(max = 500, message = "message must not exceed 500 characters")
        String message) {
}
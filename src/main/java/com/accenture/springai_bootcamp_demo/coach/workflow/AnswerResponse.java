package com.accenture.springai_bootcamp_demo.coach.workflow;

import com.accenture.springai_bootcamp_demo.coach.progress.TopicProgress;

public record AnswerResponse(String topic, String verdict, String reason, String feedback, TopicProgress progress) {
}
package com.accenture.springai_bootcamp_demo.coach;

import java.util.List;

/**
 * A generated quiz. The model's JSON output is mapped straight onto this record (Spring AI structured output).
 */
public record QuizResponse(String topic, List<QuizQuestion> questions) {
}
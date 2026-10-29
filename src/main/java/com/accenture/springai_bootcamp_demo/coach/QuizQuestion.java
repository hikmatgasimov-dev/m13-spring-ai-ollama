package com.accenture.springai_bootcamp_demo.coach;

import java.util.List;

/**
 * One multiple-choice question. {@code correctIndex} is the 0-based index into {@code options}.
 */
public record QuizQuestion(String question, List<String> options, int correctIndex, String explanation) {
}
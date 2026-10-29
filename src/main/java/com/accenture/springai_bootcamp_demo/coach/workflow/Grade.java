package com.accenture.springai_bootcamp_demo.coach.workflow;

/**
 * The checked result of the grader agent.
 */
public record Grade(Verdict verdict, String reason) {
}
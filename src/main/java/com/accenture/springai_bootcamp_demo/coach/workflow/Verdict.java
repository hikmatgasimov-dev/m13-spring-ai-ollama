package com.accenture.springai_bootcamp_demo.coach.workflow;

import java.util.Arrays;
import java.util.Optional;

/**
 * How well a trainee answered. Each verdict is worth points out of {@link #MAX_POINTS}.
 */
public enum Verdict {
    CORRECT(2),
    PARTIAL(1),
    WRONG(0);

    public static final int MAX_POINTS = 2;

    private final int points;

    Verdict(int points) {
        this.points = points;
    }

    public int points() {
        return points;
    }

    /** Reads the model's text ("Correct", " partial ") leniently; empty if it is none of the three. */
    public static Optional<Verdict> parse(String text) {
        if (text == null) {
            return Optional.empty();
        }
        String normalized = text.trim().toUpperCase();
        return Arrays.stream(values()).filter(v -> v.name().equals(normalized)).findFirst();
    }
}
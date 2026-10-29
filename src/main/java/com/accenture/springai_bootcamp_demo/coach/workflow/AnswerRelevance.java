package com.accenture.springai_bootcamp_demo.coach.workflow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

final class AnswerRelevance {

    private static final int MIN_WORD_LENGTH = 5;
    private static final Set<String> FILLER_WORDS =
            Set.of("about", "their", "there", "which", "would", "could", "should", "these", "those", "other",
                    "where", "while", "being", "after", "before");
    private static final Pattern NON_LETTERS = Pattern.compile("[^\\p{L}\\p{N}+]+");

    private AnswerRelevance() {
    }

    static boolean sharesContentWith(String notes, String answer) {
        Set<String> noteWords = new HashSet<>(Arrays.asList(words(notes)));
        return Arrays.stream(words(answer)).anyMatch(noteWords::contains);
    }

    private static String[] words(String text) {
        return Arrays.stream(NON_LETTERS.split(text.toLowerCase(Locale.ROOT)))
                .filter(word -> word.length() >= MIN_WORD_LENGTH && !FILLER_WORDS.contains(word))
                .toArray(String[]::new);
    }
}
package com.accenture.springai_bootcamp_demo.coach;

/**
 * Raised when the model could not produce a valid quiz, even after retrying.
 */
public class QuizGenerationException extends RuntimeException {

    public QuizGenerationException(String message) {
        super(message);
    }

    public QuizGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
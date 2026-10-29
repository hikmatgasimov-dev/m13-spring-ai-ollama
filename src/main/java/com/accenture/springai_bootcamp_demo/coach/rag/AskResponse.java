package com.accenture.springai_bootcamp_demo.coach.rag;

import java.util.List;

/**
 * An answer built only from the study notes, plus the note sections it was built from.
 */
public record AskResponse(String question, String answer, List<Source> sources) {

    public record Source(String file, String section) {
    }
}
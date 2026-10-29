package com.accenture.springai_bootcamp_demo.coach.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AgentValidationTest {

    @Test
    void question_mustEndWithQuestionMark() {
        assertThat(QuestionAgent.findProblem("What is the default bean scope?")).isNull();
        assertThat(QuestionAgent.findProblem("Explain bean scopes.")).isEqualTo("reply is not a question");
        assertThat(QuestionAgent.findProblem("  ")).isEqualTo("empty reply");
        assertThat(QuestionAgent.findProblem(null)).isEqualTo("empty reply");
    }

    @Test
    void grade_isUsableOnlyWithKnownVerdictAndReason() {
        assertThat(GraderAgent.toGrade(new GradeDraft("Correct", " Matches the notes. ")))
                .isEqualTo(new Grade(Verdict.CORRECT, "Matches the notes."));
        assertThat(GraderAgent.toGrade(new GradeDraft("maybe", "unsure"))).isNull();
        assertThat(GraderAgent.toGrade(new GradeDraft("wrong", " "))).isNull();
        assertThat(GraderAgent.toGrade(null)).isNull();
    }
}
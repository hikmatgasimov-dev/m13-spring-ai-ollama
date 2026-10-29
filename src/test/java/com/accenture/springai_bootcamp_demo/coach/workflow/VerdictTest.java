package com.accenture.springai_bootcamp_demo.coach.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VerdictTest {

    @Test
    void parse_ignoresCaseAndSpaces() {
        assertThat(Verdict.parse(" Correct ")).contains(Verdict.CORRECT);
        assertThat(Verdict.parse("partial")).contains(Verdict.PARTIAL);
        assertThat(Verdict.parse("WRONG")).contains(Verdict.WRONG);
    }

    @Test
    void parse_rejectsUnknownOrMissingText() {
        assertThat(Verdict.parse("almost")).isEmpty();
        assertThat(Verdict.parse("")).isEmpty();
        assertThat(Verdict.parse(null)).isEmpty();
    }

    @Test
    void points_areOutOfMaxPoints() {
        assertThat(Verdict.CORRECT.points()).isEqualTo(Verdict.MAX_POINTS);
        assertThat(Verdict.PARTIAL.points()).isEqualTo(1);
        assertThat(Verdict.WRONG.points()).isZero();
    }
}
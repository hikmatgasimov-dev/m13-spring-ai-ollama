package com.accenture.springai_bootcamp_demo.coach.progress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private QuizScoreRepository repository;

    @InjectMocks
    private ProgressService service;

    @Test
    void save_normalizesTopic() {
        when(repository.save(any(QuizScore.class))).thenAnswer(i -> i.getArgument(0));

        QuizScore saved = service.save("  JPA ", 2, 3);

        assertThat(saved.getTopic()).isEqualTo("jpa");
        assertThat(saved.getScore()).isEqualTo(2);
        assertThat(saved.getTotal()).isEqualTo(3);
    }

    @Test
    void save_rejectsScoreHigherThanTotal() {
        assertThatThrownBy(() -> service.save("jpa", 5, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be between 0 and 3");
        verify(repository, never()).save(any());
    }

    @Test
    void progress_calculatesAverageAndBestPerTopic() {
        when(repository.findAll()).thenReturn(List.of(
                QuizScore.create("jpa", 1, 4),      // 25 %
                QuizScore.create("jpa", 3, 4),      // 75 %
                QuizScore.create("testing", 3, 3)));  // 100 %

        List<TopicProgress> progress = service.progress();

        assertThat(progress).containsExactly(
                new TopicProgress("jpa", 2, 50, 75),
                new TopicProgress("testing", 1, 100, 100));
    }

    @Test
    void weakestTopic_isTheOneWithLowestAverage() {
        when(repository.findAll()).thenReturn(List.of(
                QuizScore.create("jpa", 1, 4),
                QuizScore.create("testing", 3, 3)));

        assertThat(service.weakestTopic()).map(TopicProgress::topic).contains("jpa");
    }

    @Test
    void weakestTopic_isEmptyWithoutResults() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.weakestTopic()).isEmpty();
    }
}
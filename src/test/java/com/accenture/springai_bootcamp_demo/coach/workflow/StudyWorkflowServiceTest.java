package com.accenture.springai_bootcamp_demo.coach.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.accenture.springai_bootcamp_demo.coach.progress.ProgressService;
import com.accenture.springai_bootcamp_demo.coach.progress.QuizScore;
import com.accenture.springai_bootcamp_demo.coach.progress.TopicProgress;
import com.accenture.springai_bootcamp_demo.coach.rag.NotesRetriever;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

@ExtendWith(MockitoExtension.class)
class StudyWorkflowServiceTest {

    private static final List<Document> NOTES = List.of(new Document("Lazy loading\nData is loaded on access."));

    @Mock
    private NotesRetriever retriever;
    @Mock
    private QuestionAgent questionAgent;
    @Mock
    private GraderAgent graderAgent;
    @Mock
    private CoachAgent coachAgent;
    @Mock
    private ProgressService progressService;

    @InjectMocks
    private StudyWorkflowService workflow;

    @Test
    void start_returnsTheQuestionOfTheQuestionAgent() {
        when(retriever.find("jpa")).thenReturn(NOTES);
        when(questionAgent.ask("jpa", "Lazy loading\nData is loaded on access.")).thenReturn("What is lazy loading?");

        StartSessionResponse response = workflow.start(new StartSessionRequest(" jpa "));

        assertThat(response).isEqualTo(new StartSessionResponse("jpa", "What is lazy loading?"));
    }

    @Test
    void start_failsWhenNotesDoNotCoverTheTopic() {
        when(retriever.find("cooking")).thenReturn(List.of());

        assertThatThrownBy(() -> workflow.start(new StartSessionRequest("cooking")))
                .isInstanceOf(TopicNotCoveredException.class);
        verify(questionAgent, never()).ask(any(), any());
    }

    @Test
    void answer_savesPointsOfTheVerdictAndReturnsFeedback() {
        AnswerRequest request = new AnswerRequest("jpa", "What is lazy loading?", "Data is loaded when accessed");
        Grade grade = new Grade(Verdict.CORRECT, "Matches the notes.");
        TopicProgress progress = new TopicProgress("jpa", 1, 100, 100);
        when(retriever.find("What is lazy loading?")).thenReturn(NOTES);
        when(graderAgent.grade(any(), any(), any())).thenReturn(grade);
        when(progressService.save("jpa", 2, 2)).thenReturn(QuizScore.create("jpa", 2, 2));
        when(progressService.progress()).thenReturn(List.of(progress));
        when(coachAgent.feedback(any(), any(), any(), any())).thenReturn("Well done!");

        AnswerResponse response = workflow.answer(request);

        assertThat(response).isEqualTo(new AnswerResponse("jpa", "correct", "Matches the notes.", "Well done!", progress));
    }

    @Test
    void answer_stillSucceedsWhenTheCoachAgentFails() {
        AnswerRequest request = new AnswerRequest("jpa", "What is lazy loading?", "I do not know");
        TopicProgress progress = new TopicProgress("jpa", 1, 0, 0);
        when(retriever.find(any())).thenReturn(NOTES);
        when(graderAgent.grade(any(), any(), any())).thenReturn(new Grade(Verdict.WRONG, "Does not answer."));
        when(progressService.save("jpa", 0, 2)).thenReturn(QuizScore.create("jpa", 0, 2));
        when(progressService.progress()).thenReturn(List.of(progress));
        when(coachAgent.feedback(any(), any(), any(), any())).thenThrow(new IllegalStateException("model down"));

        AnswerResponse response = workflow.answer(request);

        assertThat(response.verdict()).isEqualTo("wrong");
        assertThat(response.feedback()).isEqualTo("Does not answer.");
    }
}
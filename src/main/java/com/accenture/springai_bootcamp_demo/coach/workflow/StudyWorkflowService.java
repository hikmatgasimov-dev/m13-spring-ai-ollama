package com.accenture.springai_bootcamp_demo.coach.workflow;

import com.accenture.springai_bootcamp_demo.client.OpenRouterException;
import com.accenture.springai_bootcamp_demo.coach.progress.ProgressService;
import com.accenture.springai_bootcamp_demo.coach.progress.QuizScore;
import com.accenture.springai_bootcamp_demo.coach.progress.TopicProgress;
import com.accenture.springai_bootcamp_demo.coach.rag.NotesRetriever;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class StudyWorkflowService {

    private final NotesRetriever retriever;
    private final QuestionAgent questionAgent;
    private final GraderAgent graderAgent;
    private final CoachAgent coachAgent;
    private final ProgressService progressService;

    public StartSessionResponse start(StartSessionRequest request) {
        String topic = request.topic().trim();
        String question = run(() -> questionAgent.ask(topic, notesFor("Java study notes about " + topic, topic)));
        return new StartSessionResponse(topic, question);
    }

    public AnswerResponse answer(AnswerRequest request) {
        String topic = request.topic().trim();
        Grade grade = run(() -> graderAgent.grade(
                notesFor(request.question(), request.question()), request.question(), request.answer()));
        log.info("Graded answer on '{}': {} ({})", topic, grade.verdict(), grade.reason());

        QuizScore saved = progressService.save(topic, grade.verdict().points(), Verdict.MAX_POINTS);
        TopicProgress progress = progressService.progress().stream()
                .filter(p -> p.topic().equals(saved.getTopic()))
                .findFirst()
                .orElseThrow();

        return new AnswerResponse(progress.topic(), grade.verdict().name().toLowerCase(), grade.reason(),
                feedbackFor(request, grade, progress), progress);
    }

    /** The grade is already saved, so a failing coach agent must not fail the whole request. */
    private String feedbackFor(AnswerRequest request, Grade grade, TopicProgress progress) {
        try {
            return coachAgent.feedback(request.question(), request.answer(), grade, progress);
        } catch (RuntimeException ex) {
            log.warn("Coach agent failed, using the grader's reason instead", ex);
            return grade.reason();
        }
    }

    private String notesFor(String query, String subject) {
        List<Document> matches = retriever.find(query);
        if (matches.isEmpty()) {
            throw new TopicNotCoveredException("The study notes contain nothing about '%s'".formatted(subject));
        }
        return matches.stream().map(Document::getText).collect(Collectors.joining("\n\n---\n\n"));
    }

    /** Domain exceptions pass through; anything else (model or embedding not reachable) becomes a 502. */
    private static <T> T run(Supplier<T> step) {
        try {
            return step.get();
        } catch (TopicNotCoveredException | AgentOutputException | OpenRouterException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Workflow step failed", ex);
            throw new OpenRouterException("Failed to reach the model: " + ex.getMessage(), ex);
        }
    }
}
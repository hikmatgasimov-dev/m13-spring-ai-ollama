package com.accenture.springai_bootcamp_demo.coach;

import com.accenture.springai_bootcamp_demo.coach.progress.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.accenture.springai_bootcamp_demo.coach.rag.AskRequest;
import com.accenture.springai_bootcamp_demo.coach.rag.AskResponse;
import com.accenture.springai_bootcamp_demo.coach.rag.NotesAnswerService;

import java.util.List;

/**
 * REST API of the study coach. Thin transport adapter over {@link CoachService}.
 */
@RestController
@RequestMapping("/api/coach")
@AllArgsConstructor
public class CoachController {

    private final CoachService coachService;
    private final NotesAnswerService notesAnswerService;
    private final CoachChatService coachChatService;
    private final ProgressService progressService;

    @PostMapping("/explain")
    public ExplainResponse explain(@Valid @RequestBody ExplainRequest request) {
        return coachService.explain(request);
    }

    @PostMapping("/quiz")
    public QuizResponse quiz(@Valid @RequestBody QuizRequest request) {
        return coachService.generateQuiz(request);
    }
    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return notesAnswerService.ask(request);
    }
    @PostMapping("/chat")
    public CoachChatResponse chat(@Valid @RequestBody CoachChatRequest request) {
        return coachChatService.chat(request);
    }

    @PostMapping("/scores")
    public TopicProgress saveScore(@Valid @RequestBody SaveScoreRequest request) {
        progressService.save(request.topic(), request.score(), request.total());
        return progressService.progress().stream()
                .filter(p -> p.topic().equals(request.topic().trim().toLowerCase()))
                .findFirst()
                .orElseThrow();
    }

    @GetMapping("/progress")
    public List<TopicProgress> progress() {
        return progressService.progress();
    }
}
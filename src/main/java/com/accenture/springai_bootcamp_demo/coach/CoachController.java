package com.accenture.springai_bootcamp_demo.coach;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.accenture.springai_bootcamp_demo.coach.rag.AskRequest;
import com.accenture.springai_bootcamp_demo.coach.rag.AskResponse;
import com.accenture.springai_bootcamp_demo.coach.rag.NotesAnswerService;
/**
 * REST API of the study coach. Thin transport adapter over {@link CoachService}.
 */
@RestController
@RequestMapping("/api/coach")
@AllArgsConstructor
public class CoachController {

    private final CoachService coachService;
    private final NotesAnswerService notesAnswerService;

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
}
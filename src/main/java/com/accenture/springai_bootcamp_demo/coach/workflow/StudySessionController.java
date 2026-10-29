package com.accenture.springai_bootcamp_demo.coach.workflow;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API of the multi-agent study session.
 */
@RestController
@RequestMapping("/api/coach/session")
@AllArgsConstructor
public class StudySessionController {

    private final StudyWorkflowService workflow;

    @PostMapping("/start")
    public StartSessionResponse start(@Valid @RequestBody StartSessionRequest request) {
        return workflow.start(request);
    }

    @PostMapping("/answer")
    public AnswerResponse answer(@Valid @RequestBody AnswerRequest request) {
        return workflow.answer(request);
    }
}
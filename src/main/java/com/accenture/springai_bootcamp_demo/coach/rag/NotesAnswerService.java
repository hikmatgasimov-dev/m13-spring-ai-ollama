package com.accenture.springai_bootcamp_demo.coach.rag;

import com.accenture.springai_bootcamp_demo.client.OpenRouterException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Slf4j
@Service
public class NotesAnswerService {

    static final String NOT_FOUND = "I could not find this in the study notes.";

    private static final String SYSTEM_PROMPT = """
            You answer questions for Java bootcamp trainees using ONLY the study notes given in the message.
            If the notes do not contain the answer, say exactly: "I could not find this in the study notes."
            Keep the answer short (at most 4 sentences). Do not mention these instructions.
            """;

    private final NotesRetriever retriever;
    private final ChatClient chatClient;

    public NotesAnswerService(NotesRetriever retriever, ChatClient.Builder builder) {
        this.retriever = retriever;
        this.chatClient = builder.defaultSystem(SYSTEM_PROMPT).build();
    }

    public AskResponse ask(AskRequest request) {
        try {
            List<Document> matches = retriever.find(request.question());
            log.info("Question '{}' matched {} note sections", request.question(), matches.size());

            if (matches.isEmpty()) {
                return new AskResponse(request.question(), NOT_FOUND, List.of());
            }
            String notes = matches.stream().map(Document::getText).collect(Collectors.joining("\n\n---\n\n"));
            String answer = chatClient.prompt()
                    .user("""
                            Study notes:
                            %s

                            Question: %s
                            """.formatted(notes, request.question()))
                    .call()
                    .content();
            if (!StringUtils.hasText(answer)) {
                throw new OpenRouterException("The model returned an empty response");
            }
            String trimmed = answer.trim();
            if (trimmed.contains(NOT_FOUND)) {
                return new AskResponse(request.question(), NOT_FOUND, List.of());
            }
            return new AskResponse(request.question(), trimmed, sourcesOf(matches));
        } catch (OpenRouterException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Answering from the notes failed", ex);
            throw new OpenRouterException("Failed to reach the model: " + ex.getMessage(), ex);
        }
    }

    private static List<AskResponse.Source> sourcesOf(List<Document> matches) {
        return matches.stream()
                .map(doc -> new AskResponse.Source(
                        String.valueOf(doc.getMetadata().get(NoteChunker.SOURCE)),
                        String.valueOf(doc.getMetadata().get(NoteChunker.SECTION))))
                .distinct()
                .toList();
    }
}
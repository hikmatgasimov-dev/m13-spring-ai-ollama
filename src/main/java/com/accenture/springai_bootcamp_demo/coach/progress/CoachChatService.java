package com.accenture.springai_bootcamp_demo.coach.progress;

import com.accenture.springai_bootcamp_demo.client.OpenRouterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Free-text chat with the coach. The model may call {@link CoachTools} to save and read quiz results.
 */
@Slf4j
@Service
public class CoachChatService {

    private static final String SYSTEM_PROMPT = """
            You are Study Coach, a friendly assistant for Java bootcamp trainees.
            Use the tools to save quiz results and to read the trainee's progress.
            Never invent scores or progress: take them only from the tools.
            If a tool reports an error, explain it to the trainee in one sentence.
            Keep replies short (at most 3 sentences).
            """;

    private final ChatClient chatClient;
    private final CoachTools coachTools;

    public CoachChatService(ChatClient.Builder builder, CoachTools coachTools) {
        this.chatClient = builder.defaultSystem(SYSTEM_PROMPT).build();
        this.coachTools = coachTools;
    }

    public CoachChatResponse chat(CoachChatRequest request) {
        try {
            String reply = chatClient.prompt()
                    .user(request.message())
                    .tools(coachTools)
                    .call()
                    .content();
            if (!StringUtils.hasText(reply)) {
                throw new OpenRouterException("The model returned an empty response");
            }
            return new CoachChatResponse(request.message(), reply.trim());
        } catch (OpenRouterException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Coach chat failed", ex);
            throw new OpenRouterException("Failed to reach the model: " + ex.getMessage(), ex);
        }
    }
}
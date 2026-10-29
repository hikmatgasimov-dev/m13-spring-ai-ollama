package com.accenture.springai_bootcamp_demo.coach.rag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Reads the study notes (src/main/resources/notes/*.md), cuts them into chunks and stores them in the vector store.
 * Done lazily on first use, so the application still starts when Ollama is not running yet.
 */
@Slf4j
@Component
public class NotesIndexer {

    private final VectorStore vectorStore;
    private boolean indexed;

    public NotesIndexer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public synchronized void ensureIndexed() {
        if (indexed) {
            return;
        }
        List<Document> chunks = new ArrayList<>();
        try {
            for (Resource note : new PathMatchingResourcePatternResolver().getResources("classpath:notes/*.md")) {
                String text = new String(note.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                chunks.addAll(NoteChunker.chunk(note.getFilename(), text));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read the study notes", ex);
        }
        if (chunks.isEmpty()) {
            throw new IllegalStateException("No study notes found in src/main/resources/notes");
        }
        vectorStore.add(chunks);   // calls the embedding model once per chunk
        indexed = true;
        log.info("Indexed {} note chunks", chunks.size());
    }
}
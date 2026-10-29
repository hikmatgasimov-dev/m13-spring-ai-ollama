package com.accenture.springai_bootcamp_demo.coach.rag;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Finds the note sections that match a text. Shared by everything that needs the study notes.
 */
@Component
public class NotesRetriever {

    private final VectorStore vectorStore;
    private final NotesIndexer indexer;
    private final int topK;
    private final double similarityThreshold;

    public NotesRetriever(VectorStore vectorStore,
                          NotesIndexer indexer,
                          @Value("${coach.rag.top-k:3}") int topK,
                          @Value("${coach.rag.similarity-threshold:0.4}") double similarityThreshold) {
        this.vectorStore = vectorStore;
        this.indexer = indexer;
        this.topK = topK;
        this.similarityThreshold = similarityThreshold;
    }

    public List<Document> find(String query) {
        indexer.ensureIndexed();
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(similarityThreshold)
                .build());
    }
}
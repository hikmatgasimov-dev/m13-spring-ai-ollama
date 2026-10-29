package com.accenture.springai_bootcamp_demo.coach.rag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;


public final class NoteChunker {

    static final String SOURCE = "source";
    static final String SECTION = "section";

    private NoteChunker() {
    }

    public static List<Document> chunk(String fileName, String markdown) {
        List<Document> chunks = new ArrayList<>();
        String section = null;
        StringBuilder body = new StringBuilder();

        for (String line : markdown.split("\\R")) {
            if (line.startsWith("## ")) {
                addChunk(chunks, fileName, section, body);
                section = line.substring(3).trim();
                body = new StringBuilder();
            } else if (section != null) {
                body.append(line).append('\n');
            }
        }
        addChunk(chunks, fileName, section, body);
        return chunks;
    }

    private static void addChunk(List<Document> chunks, String fileName, String section, StringBuilder body) {
        if (section == null || body.toString().isBlank()) {
            return;
        }
        String text = section + "\n" + body.toString().trim();
        chunks.add(new Document(text, Map.of(SOURCE, fileName, SECTION, section)));
    }
}
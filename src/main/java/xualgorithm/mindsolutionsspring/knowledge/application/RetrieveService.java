package xualgorithm.mindsolutionsspring.knowledge.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.knowledge.dto.response.RetrievedChunk;

import java.util.List;
import java.util.Map;

@Service
public class RetrieveService {

    private static final Logger log = LoggerFactory.getLogger(RetrieveService.class);

    private final VectorStore vectorStore;

    private final int topK;

    private final double similarityThreshold;

    public RetrieveService(VectorStore vectorStore, @Value("${knowledge.top-k:4}") int topK, @Value("${knowledge.similarity-threshold:0.5}") double similarityThreshold) {
        this.vectorStore = vectorStore;
        this.topK = topK;
        this.similarityThreshold = similarityThreshold;
    }

    public List<RetrievedChunk> retrieve(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(similarityThreshold)
                .build();

        List<Document> encontrados = vectorStore.similaritySearch(request);

        if (encontrados == null || encontrados.isEmpty()) {
            return List.of();
        }

        List<RetrievedChunk> chunks = encontrados.stream().map(RetrieveService::toChunk).toList();

        return chunks;
    }

    private static RetrievedChunk toChunk(Document document) {
        Map<String, Object> metadata = document.getMetadata();

        return new RetrievedChunk(
                document.getText(),
                String.valueOf(metadata.getOrDefault("source", "desconocido")),
                String.valueOf(metadata.getOrDefault("seccion", "")),
                document.getScore() == null ? 0.0 : document.getScore());
    }

}

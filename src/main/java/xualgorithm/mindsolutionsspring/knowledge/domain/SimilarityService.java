package xualgorithm.mindsolutionsspring.knowledge.domain;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class SimilarityService {

    private final EmbeddingModel embeddingModel;

    public SimilarityService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public double cosineSimilarity(String texto1, String texto2) {

        float[] vector1 = embeddingModel.embed(texto1);
        float[] vector2 = embeddingModel.embed(texto2);

        return cosineSimilarity(vector1, vector2);
    }

    private double cosineSimilarity(float[] a, float[] b) {

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }



}

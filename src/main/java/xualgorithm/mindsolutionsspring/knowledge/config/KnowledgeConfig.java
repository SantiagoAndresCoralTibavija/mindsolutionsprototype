package xualgorithm.mindsolutionsspring.knowledge.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnowledgeConfig {

    @Bean
    public SimpleVectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public TokenTextSplitter textSplitter() {
        return TokenTextSplitter.builder()
                .withChunkSize(700)
                .withMinChunkSizeChars(300)
                .withMinChunkLengthToEmbed(30)
                .withKeepSeparator(true)
                .build();
    }

}

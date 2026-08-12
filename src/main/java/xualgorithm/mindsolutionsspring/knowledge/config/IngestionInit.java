package xualgorithm.mindsolutionsspring.knowledge.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import xualgorithm.mindsolutionsspring.knowledge.application.IngestionService;
import xualgorithm.mindsolutionsspring.knowledge.dto.response.IngestionResult;

import java.io.File;

@Component
public class IngestionInit implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionInit.class);

    private final SimpleVectorStore vectorStore;
    private final IngestionService ingestionService;
    private final String storeFileName;

    public IngestionInit(SimpleVectorStore vectorStore,
                         IngestionService ingestionService,
                         @Value("${knowledge.store-file:knowledge-store.json}") String storeFileName) {
        this.vectorStore = vectorStore;
        this.ingestionService = ingestionService;
        this.storeFileName = storeFileName;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Resource serializado = new ClassPathResource(storeFileName);

            if (serializado.exists()) {
                vectorStore.load(serializado);
                log.info("Base de conocimiento cargada desde el classpath ({})", storeFileName);
                return;
            }

            log.info("No existe {} en el classpath: ejecutando la ingesta", storeFileName);

            IngestionResult result = ingestionService.ingest();

            File destino = new File(storeFileName);
            vectorStore.save(destino);

            log.warn("Indice generado con {} chunks en {}. Copiar a src/main/resources/ ",
                    result.chunkSize(), destino.getAbsolutePath());
        }
        catch (Exception e) {
            log.error("No se pudo inicializar la base de conocimiento. El chat va a funcionar "
                    + "sin RAG. Causa: {}", e.toString());
        }
    }

}

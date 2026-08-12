package xualgorithm.mindsolutionsspring.knowledge.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import xualgorithm.mindsolutionsspring.knowledge.domain.MarkdownSectionSplitter;
import xualgorithm.mindsolutionsspring.knowledge.dto.response.IngestionResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    private static final String LOCATION = "classpath:/documents/ingestion/*.md";


    private static final int MAX_SECTION_CHARS = 2800;

    private final VectorStore vectorStore;
    private final TokenTextSplitter textSplitter;
    private final MarkdownSectionSplitter sectionSplitter;

    public IngestionService(VectorStore vectorStore, TokenTextSplitter textSplitter, MarkdownSectionSplitter sectionSplitter) {
        this.vectorStore = vectorStore;
        this.textSplitter = textSplitter;
        this.sectionSplitter = sectionSplitter;
    }

    public IngestionResult ingest() throws IOException {
        Resource[] archivos = new PathMatchingResourcePatternResolver().getResources(LOCATION);

        List<Document> sections = new ArrayList<>();
        for (Resource archivo : archivos) {
            String markdown = archivo.getContentAsString(StandardCharsets.UTF_8);
            List<Document> delArchivo = sectionSplitter.split(markdown, archivo.getFilename());

            log.info("{}: {} secciones indexables", archivo.getFilename(), delArchivo.size());
            sections.addAll(delArchivo);
        }

        if (sections.isEmpty()) {
            throw new IllegalStateException("No se encontro ninguna seccion indexable en " + LOCATION);
        }

        List<Document> chunks = partirSeccionesLargas(sections);

        vectorStore.add(chunks);

        log.info("Ingesta terminada: {} archivos, {} secciones, {} chunks",
                archivos.length, sections.size(), chunks.size());

        return new IngestionResult(archivos.length, chunks.size());
    }


    private List<Document> partirSeccionesLargas(List<Document> sections) {
        List<Document> chunks = new ArrayList<>();

        for (Document section : sections) {
            String text = section.getText();

            if (text != null && text.length() > MAX_SECTION_CHARS) {
                List<Document> partes = textSplitter.apply(List.of(section));
                log.info("Seccion larga partida en {}: {}", partes.size(), section.getMetadata().get("seccion"));
                chunks.addAll(partes);
            }
            else {
                chunks.add(section);
            }
        }

        return chunks;
    }

}

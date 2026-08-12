package xualgorithm.mindsolutionsspring.knowledge;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import xualgorithm.mindsolutionsspring.knowledge.domain.MarkdownSectionSplitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownSectionSplitterTest {

    private final MarkdownSectionSplitter splitter = new MarkdownSectionSplitter();

    @Test
    void parteElCorpusEnSeccionesConMigajaYFuente() throws IOException {
        Resource[] archivos = new PathMatchingResourcePatternResolver()
                .getResources("classpath:/documents/ingestion/*.md");

        assertFalse(archivos.length == 0, "no se encontro ningun .md del corpus");

        List<Document> todas = new ArrayList<>();

        for (Resource archivo : archivos) {
            String markdown = archivo.getContentAsString(StandardCharsets.UTF_8);
            List<Document> secciones = splitter.split(markdown, archivo.getFilename());

            System.out.println("\n=========== " + archivo.getFilename()
                    + "  ->  " + secciones.size() + " secciones");

            for (Document seccion : secciones) {
                String texto = seccion.getText();
                System.out.printf("  [%4d chars] %s%n", texto.length(), seccion.getMetadata().get("seccion"));
            }

            todas.addAll(secciones);
        }

        System.out.println("\nTOTAL: " + todas.size() + " chunks");

        for (Document chunk : todas) {
            assertTrue(chunk.getMetadata().containsKey("source"), "falta 'source'");
            assertTrue(chunk.getMetadata().containsKey("seccion"), "falta 'seccion'");
            assertFalse(chunk.getText().contains("---\ntitulo:"), "el frontmatter no se limpio");
            assertFalse(chunk.getText().contains(MarkdownSectionSplitter.NO_INDEX), "se indexo una seccion marcada no-index");
        }
    }

}

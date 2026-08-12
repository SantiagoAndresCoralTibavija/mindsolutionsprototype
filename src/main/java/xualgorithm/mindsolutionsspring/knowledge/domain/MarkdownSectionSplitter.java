package xualgorithm.mindsolutionsspring.knowledge.domain;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MarkdownSectionSplitter {

    public static final String NO_INDEX = "<!-- no-index -->";

    private static final int MIN_SECTION_CHARS = 60;

    public List<Document> split(String markdown, String source) {
        String[] lines = markdown.replace("\r\n", "\n").split("\n", -1);

        int bodyStart = frontmatterEnd(lines);
        String titulo = readTitulo(lines, bodyStart, source);

        List<Document> sections = new ArrayList<>();
        StringBuilder body = new StringBuilder();
        String h2 = null;
        String h3 = null;

        for (int i = bodyStart; i < lines.length; i++) {
            String line = lines[i];

            if (isRule(line)) {
                continue;
            }

            int level = headingLevel(line);

            if (level == 0) {
                body.append(line).append('\n');
                continue;
            }

            flush(sections, source, titulo, h2, h3, body);

            String text = headingText(line);
            if (level == 1) {
                h2 = null;
                h3 = null;
            }
            else if (level == 2) {
                h2 = text;
                h3 = null;
            }
            else {
                h3 = text;
            }
        }

        flush(sections, source, titulo, h2, h3, body);

        return sections;
    }

    private void flush(List<Document> out, String source, String titulo,
                       String h2, String h3, StringBuilder body) {

        String content = body.toString().trim();
        body.setLength(0);

        if (h2 == null && h3 == null) {
            return;
        }
        if (content.length() < MIN_SECTION_CHARS || content.contains(NO_INDEX)) {
            return;
        }

        String heading = (h3 != null) ? h3 : h2;

        StringBuilder breadcrumb = new StringBuilder(titulo);
        if (h2 != null) {
            breadcrumb.append(" > ").append(h2);
        }
        if (h3 != null) {
            breadcrumb.append(" > ").append(h3);
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("source", source);
        metadata.put("titulo", titulo);
        metadata.put("seccion", breadcrumb.toString());
        metadata.put("heading", heading);

        out.add(Document.builder()
                .text(breadcrumb + "\n\n" + content)
                .metadata(metadata)
                .build());
    }

    private static int frontmatterEnd(String[] lines) {
        int open = 0;
        while (open < lines.length && lines[open].isBlank()) {
            open++;
        }

        if (open >= lines.length || !isRule(lines[open])) {
            return 0;
        }

        for (int i = open + 1; i < lines.length; i++) {
            if (isRule(lines[i])) {
                return i + 1;
            }
        }
        return 0;
    }

    private static String readTitulo(String[] lines, int bodyStart, String fallback) {
        for (int i = 0; i < bodyStart; i++) {
            String line = lines[i].trim();
            if (line.startsWith("titulo:")) {
                String value = line.substring("titulo:".length()).trim();
                if (!value.isEmpty()) {
                    return value;
                }
            }
        }
        for (int i = bodyStart; i < lines.length; i++) {
            if (headingLevel(lines[i]) == 1) {
                return headingText(lines[i]);
            }
        }
        return fallback;
    }

    private static boolean isRule(String line) {
        return line.trim().equals("---");
    }

    private static int headingLevel(String line) {
        int hashes = 0;
        while (hashes < line.length() && line.charAt(hashes) == '#') {
            hashes++;
        }
        if (hashes == 0 || hashes > 3) {
            return 0;
        }
        return (hashes < line.length() && line.charAt(hashes) == ' ') ? hashes : 0;
    }

    private static String headingText(String line) {
        return line.substring(headingLevel(line))
                .trim()
                .replaceFirst("^\\d+(\\.\\d+)*\\.?\\s*", "");
    }

}

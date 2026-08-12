package xualgorithm.mindsolutionsspring.ai.dto.response;

import lombok.Getter;
import xualgorithm.mindsolutionsspring.ai.domain.AuthorConversationContent;
import xualgorithm.mindsolutionsspring.ai.domain.ConversationContent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Proyeccion de un mensaje para las vistas.
 * El autor se mantiene como enum: la plantilla compara con author.name().
 */
@Getter
public class ConversationContentView {

    private final AuthorConversationContent author;
    private final String text;
    private final LocalDateTime sentAt;

    public ConversationContentView(ConversationContent conversationContent) {
        this.author = conversationContent.getAuthor();
        this.text = conversationContent.getText();
        this.sentAt = toLocal(conversationContent.getSentAt());
    }

    private static LocalDateTime toLocal(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}

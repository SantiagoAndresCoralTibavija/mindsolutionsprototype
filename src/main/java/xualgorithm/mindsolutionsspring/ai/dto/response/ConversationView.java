package xualgorithm.mindsolutionsspring.ai.dto.response;

import lombok.Getter;
import xualgorithm.mindsolutionsspring.ai.domain.Conversation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Proyeccion de una conversacion para las vistas.
 *
 * Las marcas de tiempo se exponen como LocalDateTime y no como Instant:
 * #temporals.format de Thymeleaf no puede aplicar un patron con fecha y hora
 * sobre un Instant, porque un Instant no tiene zona horaria asociada.
 * La conversion se hace aqui, que es el borde entre dominio y presentacion.
 */
@Getter
public class ConversationView {

    private final UUID uuid;
    private final String title;
    private final String author;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastMessageSentAt;

    public ConversationView(Conversation conversation) {
        this.uuid = conversation.getId();
        this.title = conversation.getTitle();
        this.author = conversation.getUser() != null ? conversation.getUser().getUser() : null;
        this.createdAt = toLocal(conversation.getCreatedAt());
        this.lastMessageSentAt = toLocal(conversation.getLastMessageSentAt());
    }

    private static LocalDateTime toLocal(java.time.Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}

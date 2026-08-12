package xualgorithm.mindsolutionsspring.ai.domain.exception;

public class ConversationNotFound extends RuntimeException {
    public ConversationNotFound() {
        super("Conversacion no encontrada");
    }
}

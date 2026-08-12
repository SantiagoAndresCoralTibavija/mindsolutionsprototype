package xualgorithm.mindsolutionsspring.ai.domain.exception;

public class BadResponseException extends RuntimeException {
    public BadResponseException() {
        super("Hubo un error al generar la respuesta, intente nuevamente");
    }
}

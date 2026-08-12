package xualgorithm.mindsolutionsspring.ai.domain.exception;

public class FillPromptException extends RuntimeException {
    public FillPromptException() {
        super("Llene el campo de texto con indicaciones para LIA");
    }
}

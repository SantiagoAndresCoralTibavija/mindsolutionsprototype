package xualgorithm.mindsolutionsspring.infra.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}

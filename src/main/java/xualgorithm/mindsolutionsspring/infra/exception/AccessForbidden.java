package xualgorithm.mindsolutionsspring.infra.exception;

public class AccessForbidden extends RuntimeException {
    public AccessForbidden(String message) {
        super(message);
    }
}

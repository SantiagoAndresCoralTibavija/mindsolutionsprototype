package xualgorithm.mindsolutionsspring.auth.domain.exception;

public class EmailCreatedException extends RuntimeException {
    public EmailCreatedException() {
        super("El correo ya se encuentra registrado");
    }
}

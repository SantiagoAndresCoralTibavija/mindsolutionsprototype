package xualgorithm.mindsolutionsspring.user.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("El usuario solicitado no se encuentra registrado");
    }
}

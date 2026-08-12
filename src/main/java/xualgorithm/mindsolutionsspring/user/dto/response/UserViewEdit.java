package xualgorithm.mindsolutionsspring.user.dto.response;

import lombok.Getter;
import xualgorithm.mindsolutionsspring.user.application.Role;
import xualgorithm.mindsolutionsspring.user.domain.User;

/**
 * Proyeccion de usuario para el modal de edicion.
 * Aqui el rol si va como enum, porque la plantilla lo compara contra
 * los valores de Role para marcar la opcion seleccionada.
 */
@Getter
public class UserViewEdit {

    private final Long id;
    private final String user;
    private final String email;
    private final Role role;
    private final boolean bloqueado;

    public UserViewEdit(User user) {
        this.id = user.getID();
        this.user = user.getUser();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.bloqueado = user.isBloqueado();
    }
}

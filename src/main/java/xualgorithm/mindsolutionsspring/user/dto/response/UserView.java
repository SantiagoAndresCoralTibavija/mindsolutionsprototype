package xualgorithm.mindsolutionsspring.user.dto.response;

import lombok.Getter;
import xualgorithm.mindsolutionsspring.user.domain.User;

/**
 * Proyeccion de usuario para el listado del panel de administracion.
 * El rol se expone como String para que la plantilla pueda hacer th:switch,
 * y nunca se expone el hash de la contrasena.
 */
@Getter
public class UserView {

    private final Long id;
    private final String user;
    private final String email;
    private final String role;
    private final boolean bloqueado;

    public UserView(User user) {
        this.id = user.getID();
        this.user = user.getUser();
        this.email = user.getEmail();
        this.role = user.getRole() != null ? user.getRole().name() : null;
        this.bloqueado = user.isBloqueado();
    }
}

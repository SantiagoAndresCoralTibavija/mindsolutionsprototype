package xualgorithm.mindsolutionsspring.infra.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import xualgorithm.mindsolutionsspring.ai.domain.exception.BadResponseException;
import xualgorithm.mindsolutionsspring.ai.domain.exception.ConversationNotFound;
import xualgorithm.mindsolutionsspring.user.domain.exception.UserNotFoundException;

/**
 * Manejo central de errores.
 *
 * Casi todas las peticiones de esta app las hace htmx pidiendo un fragmento.
 * Devolver "redirect:" a una peticion htmx no sirve: htmx sigue el 302 por su
 * cuenta y termina metiendo la pagina completa dentro del contenedor destino.
 * Para htmx hay que responder con la cabecera HX-Redirect; el redirect normal
 * se reserva para navegaciones del navegador.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({UserNotFoundException.class, ConversationNotFound.class})
    public String handleNotFound(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        return redirect("/dashboard", request, response);
    }

    @ExceptionHandler(AccessForbidden.class)
    public String handleAccessForbidden(AccessForbidden e, HttpServletRequest request, HttpServletResponse response) {
        log.warn("Acceso denegado en {}: {}", request.getRequestURI(), e.getMessage());
        return redirect("/dashboard", request, response);
    }

    @ExceptionHandler(BadResponseException.class)
    public String handleBadResponse(BadResponseException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "shared/_alerts :: toast";
    }

    @ExceptionHandler(DomainException.class)
    public String handleDomainException(DomainException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "shared/_alerts :: toast";
    }

    /**
     * Si la peticion viene de htmx se responde 200 con HX-Redirect y cuerpo
     * vacio; si viene del navegador, un redirect normal de Spring MVC.
     */
    private String redirect(String target, HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("HX-Request") != null) {
            response.setHeader("HX-Redirect", target);
            return "shared/_empty :: empty";
        }
        return "redirect:" + target;
    }
}

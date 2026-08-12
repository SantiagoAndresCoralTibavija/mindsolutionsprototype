package xualgorithm.mindsolutionsspring.auth.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import xualgorithm.mindsolutionsspring.auth.domain.exception.EmailCreatedException;
import xualgorithm.mindsolutionsspring.auth.domain.exception.PasswordException;
import xualgorithm.mindsolutionsspring.auth.dto.request.RegisterPost;
import xualgorithm.mindsolutionsspring.user.application.UserApplicationService;

@Controller
public class RegisterController {

    private final UserApplicationService service;

    public RegisterController(UserApplicationService service) {
        this.service = service;
    }

    @GetMapping("/registro")
    public String registerGet(Model model) {
        model.addAttribute("RegisterDTO", new RegisterPost());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute @Valid RegisterPost request,
                               BindingResult result,
                               Model model,
                               HttpSession session,
                               HttpServletResponse response) {

        if (result.hasErrors()) {
            model.addAttribute("message", "Debe llenar todos los campos");
            return "shared/_alerts :: inline";
        }

        try {
            service.createUser(request);
        } catch (EmailCreatedException | PasswordException e) {
            model.addAttribute("message", e.getMessage());
            return "shared/_alerts :: inline";
        }

        // El mensaje viaja en sesion porque htmx redirige con HX-Redirect,
        // y en ese salto no aplican los flash attributes de Spring MVC.
        session.setAttribute("successMessage", "Registro exitoso. Ya puede iniciar sesion.");
        response.setHeader("HX-Redirect", "/ingreso");

        return "shared/_empty :: empty";
    }
}

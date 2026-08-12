package xualgorithm.mindsolutionsspring.auth.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import xualgorithm.mindsolutionsspring.auth.dto.request.LoginPost;
import xualgorithm.mindsolutionsspring.infra.security.AuthService;
import xualgorithm.mindsolutionsspring.infra.security.JwtAuthFilter;
import xualgorithm.mindsolutionsspring.infra.security.JwtService;

@Controller
public class LoginController {

    private final AuthService authService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtService jwtService;

    public LoginController(AuthService authService, JwtAuthFilter jwtAuthFilter, JwtService jwtService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @GetMapping("/ingreso")
    public String loginGet(HttpServletRequest request, Model model, HttpSession session) {

        String jwt = jwtAuthFilter.getTokenFromCookies(request);

        if (jwt != null && !jwt.isBlank() && jwtService.JwtValide(jwt)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("LoginDTO", new LoginPost());

        if (session.getAttribute("successMessage") != null) {
            model.addAttribute("successMessage", session.getAttribute("successMessage"));
            session.removeAttribute("successMessage");
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String loginPost(@Valid @ModelAttribute LoginPost request, BindingResult result, HttpServletResponse response, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("message", "Debe llenar todos los campos");
            return "shared/_alerts :: inline";
        }

        try {
            String token = authService.login(request);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(jwtService.getExpirationSeconds())
                    .sameSite("Lax")
                    .build();

            response.setHeader("Set-Cookie", cookie.toString());
            response.setHeader("HX-Redirect", "/dashboard");

            return "shared/_empty :: empty";

        } catch (LockedException | DisabledException e) {
            model.addAttribute("message", "Su cuenta se encuentra bloqueada");
            return "shared/_alerts :: inline";
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            model.addAttribute("message", "Credenciales incorrectas");
            return "shared/_alerts :: inline";
        }
    }
}

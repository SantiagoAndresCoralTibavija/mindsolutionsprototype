package xualgorithm.mindsolutionsspring.dashboard.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import xualgorithm.mindsolutionsspring.ai.domain.repository.AIRepository;
import xualgorithm.mindsolutionsspring.auth.domain.exception.EmailCreatedException;
import xualgorithm.mindsolutionsspring.user.application.AuthUser;
import xualgorithm.mindsolutionsspring.user.application.Role;
import xualgorithm.mindsolutionsspring.user.application.UserApplicationService;
import xualgorithm.mindsolutionsspring.user.application.UserQueryService;
import xualgorithm.mindsolutionsspring.user.domain.User;
import xualgorithm.mindsolutionsspring.user.domain.UserRepository;
import xualgorithm.mindsolutionsspring.user.domain.exception.UserNotFoundException;
import xualgorithm.mindsolutionsspring.user.dto.request.CreatePost;
import xualgorithm.mindsolutionsspring.user.dto.request.EditPost;
import xualgorithm.mindsolutionsspring.user.dto.response.UserViewEdit;

@Controller
@PreAuthorize("hasAnyRole('ROOT')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository repository;
    private final AIRepository aiRepository;
    private final UserApplicationService service;
    private final UserQueryService userQueryService;


    @ModelAttribute("roles")
    public Role[] roles() {
        return Role.values();
    }

    @GetMapping("/dashboard/admin")
    public String getAdminDashboard() {
        return "admin/index :: page";
    }

    @GetMapping("/dashboard/right-panel/admin")
    public String getAdminDashboardRightPanel(Model model) {
        model.addAttribute("totalUsers", repository.count());
        model.addAttribute("totalConversations", aiRepository.count());
        return "admin/_sidebar :: panel";
    }

    @GetMapping("/admin/users/list")
    public String getUsers(Model model, @AuthenticationPrincipal AuthUser authUser) {
        return usersTable(model, authUser);
    }

    @GetMapping("/admin/users/create")
    public String getUserCreateModal() {
        return "admin/_user_modal :: create";
    }

    @PostMapping("/admin/users")
    public String createUser(@ModelAttribute @Valid CreatePost request,
                             BindingResult result,
                             Model model,
                             @AuthenticationPrincipal AuthUser authUser) {

        if (result.hasErrors()) {
            model.addAttribute("message", "Debe llenar todos los campos");
            return "shared/_alerts :: toast";
        }

        try {
            service.createUserDashboard(request);
        } catch (EmailCreatedException e) {
            model.addAttribute("message", e.getMessage());
            return "shared/_alerts :: toast";
        }

        return usersTable(model, authUser);
    }

    @GetMapping("/admin/users/{id}/edit")
    public String getUserEditModal(@PathVariable Long id, Model model) {
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);
        model.addAttribute("user", new UserViewEdit(user));
        return "admin/_user_modal :: edit";
    }

    @PostMapping("/admin/users/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute @Valid EditPost request,
                             @AuthenticationPrincipal AuthUser authUser,
                             Model model) {

        try {
            service.updateUser(id, request);
        } catch (EmailCreatedException | UserNotFoundException e) {
            model.addAttribute("message", e.getMessage());
            return "shared/_alerts :: toast";
        }

        return usersTable(model, authUser);
    }

    @PostMapping("/admin/users/{id}/toggle-lock")
    public String toggleUserLock(@PathVariable Long id,
                                 Model model,
                                 @AuthenticationPrincipal AuthUser authUser) {

        try {
            service.toggleUserLock(id);
        } catch (UserNotFoundException e) {
            model.addAttribute("message", e.getMessage());
            return "shared/_alerts :: toast";
        }

        return usersTable(model, authUser);
    }

    /**
     * Las cuatro acciones del panel terminan repintando la misma tabla.
     * currentUser sirve para que el administrador no se vea a si mismo en la
     * lista; la comparacion en la plantilla es por id, nunca por nombre.
     */
    private String usersTable(Model model, AuthUser authUser) {
        model.addAttribute("users", userQueryService.getAllUserList());
        model.addAttribute("currentUser", userQueryService.getUserDTO(authUser.getUserID()));
        return "admin/_users_list :: rows";
    }
}

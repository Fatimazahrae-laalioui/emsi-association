package ma.PFA.Controller;

import ma.PFA.entity.User;
import ma.PFA.repository.PublicationRepository;
import ma.PFA.repository.ForumRepository;
import ma.PFA.service.UserService;
import ma.PFA.service.EvenementService;
import ma.PFA.service.ForumService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final PublicationRepository publicationRepository;
    private final EvenementService evenementService;
    private final ForumService forumService;
    private final ForumRepository forumRepository;

    @Autowired
    public AuthController(UserService userService, PublicationRepository publicationRepository,
                          EvenementService evenementService, ForumService forumService, ForumRepository forumRepository) {
        this.userService = userService;
        this.publicationRepository = publicationRepository;
        this.evenementService = evenementService;
        this.forumService = forumService;
        this.forumRepository = forumRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
        }
        if (logout != null) {
            model.addAttribute("message", "Déconnexion réussie");
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              @RequestParam(value = "redirect", required = false) String redirectUrl,
                              HttpSession session,
                              Model model) {

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            session.setAttribute("user", user);

            // ✅ Redirection prioritaire si paramètre présent
            if (redirectUrl != null && !redirectUrl.isBlank()) {
                return "redirect:" + redirectUrl;
            }

            // ✅ Sinon : comportement par rôle
            if ("ROLE_ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/etudiant/dashboard";
            }
        } else {
            model.addAttribute("error", "Email ou mot de passe incorrect");
            return "login";
        }
    }


    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email déjà utilisé");
            return "register";
        }
        user.setRole("ROLE_STUDENT");
        userService.saveUser(user);
        return "redirect:/login?success";
    }

    // ✅ ❌ SUPPRIMÉ car déjà dans EtudiantDashboardController :
    // @GetMapping("/etudiant/dashboard")
    // public String etudiantDashboard(...) { ... }
}

package ma.PFA.Controller;

import ma.PFA.entity.Evenement;
import ma.PFA.entity.User;
import ma.PFA.service.EvenementService;
import ma.PFA.service.InscriptionService;
import ma.PFA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/evenements")
public class EvenementController {

    private final EvenementService evenementService;
    private final InscriptionService inscriptionService;
    private final UserService userService;

    @Autowired
    public EvenementController(EvenementService evenementService,
                               InscriptionService inscriptionService,
                               UserService userService) {
        this.evenementService = evenementService;
        this.inscriptionService = inscriptionService;
        this.userService = userService;
    }

    @GetMapping
    public String listEvents(Model model, Principal principal) {
        List<Evenement> evenements = evenementService.getAllEvents();
        model.addAttribute("evenements", evenements);

        if (principal != null) {
            User user = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            model.addAttribute("user", user);

            if ("ROLE_ADMIN".equals(user.getRole())) {
                return "evenements_admin";
            }
        }

        return "evenements_etudiant";
    }


    @GetMapping("/{id}")
    public String viewEvent(@PathVariable Long id, Model model, Principal principal) {
        Evenement event = evenementService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        model.addAttribute("event", event);

        if (principal != null) {
            User user = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            boolean isRegistered = inscriptionService.isUserRegistered(user, event);
            model.addAttribute("isRegistered", isRegistered);
            model.addAttribute("user", user);
        }

        return "event-details";
    }


    @PostMapping("/{id}/cancel")
    public String cancelRegistration(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Evenement event = evenementService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        inscriptionService.cancelRegistration(
                inscriptionService.getUserRegistrations(user).stream()
                        .filter(r -> r.getEvenement().equals(event))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Inscription non trouvée"))
                        .getId()
        );

        return "redirect:/evenements/" + id + "?cancel";
    }

    @PostMapping("/{id}/register")
    public String registerForEvent(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Evenement event = evenementService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        inscriptionService.registerForEvent(user, event);
        return "redirect:/evenements?success";
    }
}
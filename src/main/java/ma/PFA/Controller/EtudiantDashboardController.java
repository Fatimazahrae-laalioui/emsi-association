package ma.PFA.Controller;

import jakarta.servlet.http.HttpSession;
import ma.PFA.entity.*;
import ma.PFA.repository.*;
import ma.PFA.service.EvenementService;
import ma.PFA.service.ForumService;
import ma.PFA.service.PublicationService;
import ma.PFA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/etudiant")
public class EtudiantDashboardController {

    private final PublicationRepository publicationRepository;
    private final PublicationService publicationService;
    private final EvenementService evenementService;
    private final ForumRepository forumRepository;
    private final ForumReplyRepository forumReplyRepository;
    private final ForumService forumService;
    private final UserService userService;
    private final CommentaireRepository commentaireRepository;
    private final InscriptionRepository inscriptionRepository;

    @Autowired
    public EtudiantDashboardController(
            PublicationRepository publicationRepository,
            PublicationService publicationService,
            EvenementService evenementService,
            ForumRepository forumRepository,
            ForumReplyRepository forumReplyRepository,
            ForumService forumService,
            UserService userService,
            CommentaireRepository commentaireRepository,
            InscriptionRepository inscriptionRepository) {
        this.publicationRepository = publicationRepository;
        this.publicationService = publicationService;
        this.evenementService = evenementService;
        this.forumRepository = forumRepository;
        this.forumReplyRepository = forumReplyRepository;
        this.forumService = forumService;
        this.userService = userService;
        this.commentaireRepository = commentaireRepository;
        this.inscriptionRepository = inscriptionRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("publications", publicationService.getAllPublications());
        model.addAttribute("forums", forumService.getAllForums());
        model.addAttribute("evenements", evenementService.getAllEvents());
        model.addAttribute("membresActifs", userService.getMembresActifs());

        // 🔑 Charge les notifications NON LUES pour la cloche
        List<Inscription> inscriptions = inscriptionRepository
                .findByUserAndStatusOrUserAndLuFalse(user, InscriptionStatus.CONFIRMED, user);

        model.addAttribute("inscriptions", inscriptions);
        return "etudiantdashboard";
    }

    @PostMapping("/notifications/mark-as-read")
    public String markNotificationsAsRead(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Inscription> notifications = inscriptionRepository.findByUserAndLuFalse(user);
        for (Inscription ins : notifications) {
            ins.setLu(true);
        }
        inscriptionRepository.saveAll(notifications);

        return "redirect:/etudiant/dashboard";
    }



    @PostMapping("/publication")
    public String creerPublication(@RequestParam String contenu,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Publication publication = new Publication();
        publication.setAuteur(user);
        publication.setContenu(contenu);
        publication.setDatePublication(LocalDateTime.now());

        publicationRepository.save(publication);
        redirectAttributes.addFlashAttribute("success", "Publication créée avec succès !");
        return "redirect:/etudiant/dashboard";
    }

    @PostMapping("/commentaire")
    public String ajouterCommentaire(@RequestParam Long forumId,
                                     @RequestParam String contenu,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        ForumPost forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum non trouvé"));

        ForumReply reply = new ForumReply();
        reply.setAuteur(user);
        reply.setPost(forum);
        reply.setContenu(contenu);
        reply.setDate(LocalDateTime.now());

        forumReplyRepository.save(reply);

        redirectAttributes.addFlashAttribute("success", "Commentaire ajouté !");
        return "redirect:/";
    }

    @PostMapping("/evenements/{id}/register")
    public String registerToEvent(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Evenement evenement = evenementService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Ici : enregistrer ou mettre à jour Inscription réelle
        Inscription inscription = evenementService.registerUserToEvenement(user, evenement);

        // ✅ Message flash seulement → PAS DE STOCKAGE DE NOTIF EN BASE
        redirectAttributes.addFlashAttribute("success", "Félicitations ! Vous êtes inscrit à l'événement !");

        // Si tu veux que l'utilisateur télécharge l'invitation tout de suite
        if (inscription.getStatus() == InscriptionStatus.CONFIRMED) {
            String downloadLink = "/etudiant/inscription/" + inscription.getId() + "/print";
            redirectAttributes.addFlashAttribute("downloadLink", downloadLink);
        }

        return "redirect:/etudiant/dashboard";
    }


    @GetMapping("/formulaire")
    public String afficherFormulaire(Model model) {
        model.addAttribute("inscription", new Inscription());
        return "etudiant_formulaire";
    }

    @PostMapping("/formulaire")
    public String soumettreFormulaire(@ModelAttribute Inscription inscription,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        inscription.setUser(user);
        inscription.setStatus(InscriptionStatus.PENDING);
        inscription.setLu(false);
        inscriptionRepository.save(inscription);

        // Message succès simple
        redirectAttributes.addFlashAttribute("success", "Votre demande a bien été enregistrée !");

        // Si besoin : tu peux décider d'ajouter un lien spécifique si l'inscription est CONFIRMÉE dès maintenant (exemple)
        if (inscription.getStatus() == InscriptionStatus.CONFIRMED) {
            String downloadLink = "/etudiant/inscription/" + inscription.getId() + "/print";
            redirectAttributes.addFlashAttribute("downloadLink", downloadLink);
        }

        return "redirect:/etudiant/dashboard";
    }



    @GetMapping("/inscription/{id}/print")
    public String printInvitation(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        if (!inscription.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Accès interdit");
        }

        // ✅ Marque comme lue quand l'utilisateur télécharge
        inscription.setLu(true);
        inscriptionRepository.save(inscription);

        model.addAttribute("inscription", inscription);
        return "invitation_template";
    }


    @PostMapping("/commentaire-page")
    public String ajouterCommentaireGeneral(@RequestParam String contenu,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login?redirect=/#commentaires";

        Commentaire commentaire = new Commentaire();
        commentaire.setAuteur(user);
        commentaire.setContenu(contenu);
        commentaire.setDate(LocalDateTime.now());

        commentaireRepository.save(commentaire);

        redirectAttributes.addFlashAttribute("success", "Commentaire ajouté !");
        return "redirect:/#commentaires";
    }

    @GetMapping("/profil/{id}")
    public String voirProfil(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("etudiant", user);
        return "profil";
    }
}

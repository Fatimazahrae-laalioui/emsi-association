package ma.PFA.Controller;

import ma.PFA.entity.Evenement;
import ma.PFA.entity.Inscription;
import ma.PFA.entity.InscriptionStatus;
import ma.PFA.entity.User;
import ma.PFA.repository.EvenementRepository;
import ma.PFA.repository.InscriptionRepository;
import ma.PFA.repository.PublicationRepository;
import ma.PFA.service.EvenementService;
import ma.PFA.service.ForumService;
import ma.PFA.service.PublicationService;
import ma.PFA.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final EvenementService evenementService;
    private final PublicationService publicationService;
    private final ForumService forumService;
    private final EvenementRepository evenementRepository;
    private final InscriptionRepository inscriptionRepository;

    @Autowired
    public AdminController(UserService userService,
                           EvenementService evenementService,
                           PublicationService publicationService,
                           ForumService forumService,
                           EvenementRepository evenementRepository,
                           InscriptionRepository inscriptionRepository) {
        this.userService = userService;
        this.evenementService = evenementService;
        this.publicationService = publicationService;
        this.forumService = forumService;
        this.evenementRepository = evenementRepository;
        this.inscriptionRepository= inscriptionRepository;
    }

    // Tableau de bord
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("studentCount", userService.countByRole("ROLE_STUDENT"));
        model.addAttribute("eventCount", evenementService.countEvents());
        model.addAttribute("forumCount", forumService.getAllForums().size());
        model.addAttribute("publicationCount", publicationService.findAll().size());

        return "admin_dashboard";
    }

    // Liste des événements
    @GetMapping("/evenements")
    public String listEvenements(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("evenements", evenementService.getAllEvents());

        return "evenements_admin"; // ✅ correspond au nom du fichier HTML
    }


    // Affichage du formulaire de création d'événement
    @GetMapping("/evenements/create")
    public String showCreateEventForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("evenement", new Evenement());  // objet vide pour le formulaire
        return "create_event";  // sans "admin/"
    }


    // Traitement du formulaire de création d'événement
    @PostMapping("/evenements/create")
    public String createEvent(@ModelAttribute Evenement event,
                              @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        // Contrôle pour nbPlaces par défaut
        if (event.getNbPlaces() == null) {
            event.setNbPlaces(0);  // ou une valeur par défaut logique
        }

        String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        Path uploadPath = Paths.get("src/main/resources/static/images/uploads/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Files.copy(imageFile.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

        event.setImageFilename(filename);
        evenementRepository.save(event);

        return "redirect:/admin/evenements";
    }

    // Dans AdminController

    @GetMapping("/students")
    public String manageStudents(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("students", userService.findByRole("ROLE_STUDENT"));
        return "manage_students";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        User student = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
        model.addAttribute("student", student);
        return "edit_student";
    }

    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute User formUser, RedirectAttributes redirectAttributes) {
        try {
            // 1️⃣ Recharger l’utilisateur existant
            User existingUser = userService.findById(formUser.getId())
                    .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

            // 2️⃣ Mettre à jour seulement les champs modifiés
            existingUser.setFullName(formUser.getFullName());
            existingUser.setEmail(formUser.getEmail());
            existingUser.setRole(formUser.getRole());

            // 3️⃣ Ne jamais écraser le password si tu n’as pas de champ « nouveau mot de passe »
            // Si tu as un champ « password » dans ton form : ajoute une condition
            // if (formUser.getPassword() != null && !formUser.getPassword().isBlank()) {
            //     existingUser.setPassword(formUser.getPassword());
            // }

            userService.saveUser(existingUser);

            redirectAttributes.addFlashAttribute("success", "Étudiant mis à jour !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour !");
        }

        return "redirect:/admin/students";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addAttribute("success", true);
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", true);
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/forums")
    public String manageForums(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("forums", forumService.getAllForums());
        return "manage_forums";
    }

    @GetMapping("/forums/delete/{id}")
    public String deleteForum(@PathVariable Long id) {
        forumService.supprimerForum(id);
        return "redirect:/admin/forums";
    }

    @GetMapping("/inscriptions")
    public String manageInscriptions(Model model) {
        List<Inscription> inscriptions = inscriptionRepository.findByStatus(InscriptionStatus.PENDING);
        model.addAttribute("inscriptions", inscriptions);
        return "manage_inscriptions";
    }


    @PostMapping("/inscriptions/approve/{id}")
    public String approveInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Inscription i = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));
        i.setStatus(InscriptionStatus.CONFIRMED);
        i.setMessage("Félicitations ! Votre demande a été approuvée. Veuillez télécharger et imprimer votre invitation.");
        inscriptionRepository.save(i);
        redirectAttributes.addFlashAttribute("success", "Inscription approuvée !");
        return "redirect:/admin/inscriptions";
    }

    @PostMapping("/inscriptions/reject/{id}")
    public String rejectInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Inscription i = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));
        i.setStatus(InscriptionStatus.CANCELLED);
        i.setMessage("Désolé, votre demande a été refusée cette fois-ci.");
        inscriptionRepository.save(i);
        redirectAttributes.addFlashAttribute("success", "Inscription rejetée !");
        return "redirect:/admin/inscriptions";
    }
    @GetMapping("/publications")
    public String managePublications(Model model) {
        List<?> publications = publicationService.findAll();
        model.addAttribute("publications", publications);
        return "manage_publications";
    }

    @GetMapping("/publications/delete/{id}")
    public String deletePublication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            publicationService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Publication supprimée !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression !");
        }
        return "redirect:/admin/publications";
    }
    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("admin", admin);
        return "admin_edit_profile";
    }
    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("admin") User adminForm,
                                @RequestParam(required = false) String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User admin = (User) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }

        admin.setFullName(adminForm.getFullName());
        admin.setEmail(adminForm.getEmail());

        if (password != null && !password.isBlank()) {
            admin.setPassword(password);
        }

        userService.saveUser(admin);
        session.setAttribute("user", admin);

        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès !");
        return "redirect:/admin/profile/edit";
    }
    @GetMapping("/evenements/edit/{id}")
    public String showEditEventForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec id: " + id));

        model.addAttribute("evenement", evenement);
        model.addAttribute("user", user);
        return "edit_event"; // nom de ton fichier HTML pour le formulaire d'édition
    }
    @PostMapping("/evenements/update")
    public String updateEvent(@ModelAttribute Evenement evenement,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/images/uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(imageFile.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            evenement.setImageFilename(filename);
        }

        evenementRepository.save(evenement);
        redirectAttributes.addFlashAttribute("success", "Événement mis à jour !");
        return "redirect:/admin/evenements";
    }
    @GetMapping("/evenements/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            evenementRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Événement supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression !");
        }
        return "redirect:/admin/evenements";
    }



}

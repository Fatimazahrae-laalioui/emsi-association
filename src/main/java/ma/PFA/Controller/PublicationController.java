package ma.PFA.Controller;

import jakarta.servlet.http.HttpSession;
import ma.PFA.entity.Publication;
import ma.PFA.entity.User;
import ma.PFA.service.PublicationService;
import ma.PFA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/publications")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private UserService userService;

    @Value("${upload.dir}")
    private String uploadDir;

    // Liste des publications
    @GetMapping
    public String listPublications(Model model) {
        model.addAttribute("publications", publicationService.findAll());
        model.addAttribute("newPublication", new Publication());
        return "publications";
    }

    // Ajouter une publication
    @PostMapping("/add")
    public String addPublication(
            @ModelAttribute Publication publication,
            HttpSession session,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        User auteur = (User) session.getAttribute("user");
        if (auteur == null) {
            throw new RuntimeException("Utilisateur non connecté !");
        }

        publication.setAuteur(auteur);
        publication.setDatePublication(LocalDateTime.now());

        handleFileUpload(publication, file);
        publicationService.save(publication);

        return "redirect:/publications";
    }

    @GetMapping("/edit/{id}")
    public String editPublication(@PathVariable Long id, Model model) {
        publicationService.findById(id).ifPresent(pub -> model.addAttribute("publication", pub));
        return "edit_publication";
    }

    @PostMapping("/update")
    public String updatePublication(@ModelAttribute Publication publication,
                                    @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        handleFileUpload(publication, file);
        publicationService.save(publication);
        return "redirect:/publications";
    }

    @GetMapping("/delete/{id}")
    public String deletePublication(@PathVariable Long id) {
        publicationService.deleteById(id);
        return "redirect:/publications";
    }

    private void handleFileUpload(Publication publication, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            publication.setImageUrl("/uploads/" + filename);
        }
    }

    // ✅ Social actions
    @PostMapping("/like")
    public String likePublication(@RequestParam Long publicationId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        publicationService.likePublication(publicationId, user);
        return "redirect:/etudiant/dashboard";
    }

    @PostMapping("/save")
    public String savePublication(@RequestParam Long publicationId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        publicationService.savePublicationForUser(publicationId, user);
        return "redirect:/etudiant/dashboard";
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam Long publicationId,
                             @RequestParam String contenu,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        publicationService.addComment(publicationId, user, contenu);
        return "redirect:/etudiant/dashboard";
    }
}

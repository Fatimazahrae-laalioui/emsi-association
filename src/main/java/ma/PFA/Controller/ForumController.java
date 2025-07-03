package ma.PFA.Controller;

import jakarta.servlet.http.HttpSession;
import ma.PFA.entity.ForumPost;
import ma.PFA.entity.ForumReply;
import ma.PFA.entity.User;
import ma.PFA.service.ForumService;
import ma.PFA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/forums")
public class ForumController {

    @Autowired
    private final ForumService forumService;

    @Autowired
    private final UserService userService;

    public ForumController(ForumService forumService, UserService userService) {
        this.forumService = forumService;
        this.userService = userService;
    }

    @GetMapping
    public String listForums(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<ForumPost> forums = forumService.findAllForums();
        model.addAttribute("forums", forums);

        // ✅ Corrige le rôle pour matcher ton HTML
        String role = user.getRole();
        if (role.startsWith("ROLE_")) {
            role = role.substring(5); // Enlève "ROLE_"
        }
        model.addAttribute("userRole", role);

        System.out.println("ROLE utilisé pour template : " + role);
        System.out.println("Forums trouvés : " + forums.size());

        return "forums";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("forumPost", new ForumPost());
        return "create-forum";
    }

    @PostMapping("/create")
    public String createForum(@ModelAttribute ForumPost forumPost,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User auteur = (User) session.getAttribute("user");
        if (auteur == null) {
            return "redirect:/login";
        }

        forumPost.setAuteur(auteur);
        forumPost.setDateCreation(LocalDateTime.now());
        forumService.saveForum(forumPost);

        redirectAttributes.addFlashAttribute("success", "Forum créé avec succès !");
        return "redirect:/etudiant/dashboard";
    }


    @GetMapping("/{id}")
    public String showForumDetails(@PathVariable Long id, Model model, HttpSession session) {
        ForumPost forumPost = forumService.getForumById(id)
                .orElseThrow(() -> new RuntimeException("Forum non trouvé"));

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("forum", forumPost);
        model.addAttribute("newReply", new ForumReply());
        model.addAttribute("currentUser", currentUser);

        return "forum-detail";
    }

    @PostMapping("/repondre/{id}")
    public String repondre(@PathVariable Long id, @RequestParam String reponse, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        forumService.ajouterReponse(id, reponse, user.getEmail());
        return "redirect:/forums/" + id;
    }

    @PostMapping("/like/{id}")
    public String liker(@PathVariable Long id) {
        forumService.ajouterLike(id);
        return "redirect:/forums/" + id;
    }

    @PostMapping("/delete/{id}")
    public String supprimer(@PathVariable Long id) {
        forumService.supprimerForum(id);
        return "redirect:/forums";
    }
}

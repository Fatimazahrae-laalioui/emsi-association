package ma.PFA.service;

import ma.PFA.entity.ForumPost;
import ma.PFA.entity.ForumReply;
import ma.PFA.repository.ForumReplyRepository;
import ma.PFA.repository.ForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ForumService {

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private ForumReplyRepository forumReplyRepository;


    public List<ForumPost> findAllForums() {
        return forumRepository.findAllByOrderByDateDesc();
    }

    @Autowired
    private UserService userService;

    public List<ForumPost> getAllForums() {
        return forumRepository.findAllByOrderByDateDesc();
    }

    public Optional<ForumPost> getForumById(Long id) {
        return forumRepository.findById(id);
    }

    public ForumPost createForumPost(ForumPost forumPost) {
        return forumRepository.save(forumPost);
    }
    public void deleteForumPost(Long id) {
        forumRepository.deleteById(id);
    }
    public void ajouterReponse(Long forumId, String reponse, String email) {
        ForumPost forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum non trouvé"));

        ForumReply reply = new ForumReply();
        reply.setContenu(reponse);
        reply.setPost(forum);
        reply.setDate(LocalDateTime.now());
        reply.setAuteur(userService.findByEmail(email).orElse(null));

        forum.getReponses().add(reply);
        forumRepository.save(forum);  // grâce au cascade, la réponse sera enregistrée
    }


    public void supprimerForum(Long id) {
        forumRepository.deleteById(id);
    }


    public ForumPost saveForum(ForumPost forumPost) {
        return forumRepository.save(forumPost);
    }

    public ForumPost getForumByIdWithReplies(Long id) {
        return forumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forum non trouvé"));
    }
    public void ajouterLike(Long forumId) {
        ForumPost forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum non trouvé"));
        forum.setLikeCount(forum.getLikeCount() + 1);
        forumRepository.save(forum);
    }
}

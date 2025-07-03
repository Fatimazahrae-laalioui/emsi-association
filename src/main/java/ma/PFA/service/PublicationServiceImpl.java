package ma.PFA.service;

import ma.PFA.entity.*;
import ma.PFA.repository.CommentaireRepository;
import ma.PFA.repository.LikePublicationRepository;
import ma.PFA.repository.PublicationRepository;
import ma.PFA.repository.SavedPublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;
    private final LikePublicationRepository likePublicationRepository;
    private final SavedPublicationRepository savedPublicationRepository;
    private final CommentaireRepository commentaireRepository;

    @Autowired
    public PublicationServiceImpl(
            PublicationRepository publicationRepository,
            LikePublicationRepository likePublicationRepository,
            SavedPublicationRepository savedPublicationRepository,
            CommentaireRepository commentaireRepository) {
        this.publicationRepository = publicationRepository;
        this.likePublicationRepository = likePublicationRepository;
        this.savedPublicationRepository = savedPublicationRepository;
        this.commentaireRepository = commentaireRepository;
    }

    @Override
    public List<Publication> findAll() {
        return publicationRepository.findAll();
    }

    @Override
    public List<Publication> getAllPublications() {
        return publicationRepository.findAll();
    }

    @Override
    public Optional<Publication> findById(Long id) {
        return publicationRepository.findById(id);
    }

    @Override
    public Publication save(Publication publication) {
        return publicationRepository.save(publication);
    }

    @Override
    public void deleteById(Long id) {
        publicationRepository.deleteById(id);
    }

    @Override
    public long countPublications() {
        return publicationRepository.count();
    }

    @Override
    public void likePublication(Long publicationId, User user) {
        Publication pub = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
        if (!likePublicationRepository.existsByPublicationAndUser(pub, user)) {
            LikePublication like = new LikePublication();
            like.setPublication(pub);
            like.setUser(user);
            likePublicationRepository.save(like);
        }
    }

    @Override
    public void savePublicationForUser(Long publicationId, User user) {
        Publication pub = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
        if (!savedPublicationRepository.existsByPublicationAndUser(pub, user)) {
            SavedPublication saved = new SavedPublication();
            saved.setPublication(pub);
            saved.setUser(user);
            savedPublicationRepository.save(saved);
        }
    }

    @Override
    public void addComment(Long publicationId, User user, String contenu) {
        Publication pub = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        Commentaire commentaire = new Commentaire();
        commentaire.setPublication(pub);
        commentaire.setAuteur(user);
        commentaire.setContenu(contenu);
        commentaire.setDate(LocalDateTime.now());

        commentaireRepository.save(commentaire);
    }
}

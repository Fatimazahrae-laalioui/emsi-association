package ma.PFA.service;

import ma.PFA.entity.Publication;
import ma.PFA.entity.User;

import java.util.List;
import java.util.Optional;

public interface PublicationService {
    List<Publication> findAll();
    List<Publication> getAllPublications();
    Optional<Publication> findById(Long id);
    Publication save(Publication publication);
    void deleteById(Long id);
    long countPublications();

    // ✅ Ajoutés pour réseaux sociaux
    void likePublication(Long publicationId, User user);
    void savePublicationForUser(Long publicationId, User user);
    void addComment(Long publicationId, User user, String contenu);
}

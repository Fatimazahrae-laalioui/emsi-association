package ma.PFA.repository;

import ma.PFA.entity.Commentaire;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByPublicationId(Long publicationId);
}
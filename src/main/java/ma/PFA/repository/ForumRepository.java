package ma.PFA.repository;

import ma.PFA.entity.ForumPost;  // Import de l'entité correcte
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findAllByOrderByDateDesc();

}

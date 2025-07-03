package ma.PFA.repository;

import ma.PFA.entity.ForumPost;
import ma.PFA.entity.Sujet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SujetRepository extends JpaRepository<Sujet, Long> {
    List<Sujet> findByForumOrderByDateCreationDesc(ForumPost forum);
}

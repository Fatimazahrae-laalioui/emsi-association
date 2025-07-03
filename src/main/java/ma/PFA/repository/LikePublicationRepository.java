package ma.PFA.repository;

import ma.PFA.entity.LikePublication;
import ma.PFA.entity.Publication;
import ma.PFA.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikePublicationRepository extends JpaRepository<LikePublication, Long> {
    boolean existsByPublicationAndUser(Publication publication, User user);
}
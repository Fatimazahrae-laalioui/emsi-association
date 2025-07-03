package ma.PFA.repository;


import ma.PFA.entity.Publication;
import ma.PFA.entity.SavedPublication;
import ma.PFA.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedPublicationRepository extends JpaRepository<SavedPublication, Long> {
    boolean existsByPublicationAndUser(Publication publication, User user);
}

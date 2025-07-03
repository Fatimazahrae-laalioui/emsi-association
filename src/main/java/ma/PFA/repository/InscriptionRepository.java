package ma.PFA.repository;

import ma.PFA.entity.Inscription;
import ma.PFA.entity.InscriptionStatus;
import ma.PFA.entity.User;
import ma.PFA.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByUser(User user);
    List<Inscription> findByEvenement(Evenement evenement);
    boolean existsByUserAndEvenement(User user, Evenement evenement);
    List<Inscription> findByStatus(InscriptionStatus status);
    List<Inscription> findByUserAndLuFalse(User user);
    List<Inscription> findByUserAndStatusOrUserAndLuFalse(User user1, InscriptionStatus status, User user2);

}
package ma.PFA.repository;

import ma.PFA.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    List<Evenement> findTop5ByOrderByDateDesc();
    List<Evenement> findAllByOrderByDateDesc();
    List<Evenement> findByDateAfterOrderByDateAsc(LocalDate date);

}
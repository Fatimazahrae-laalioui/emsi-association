package ma.PFA.repository;


import ma.PFA.entity.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
        List<Publication> findAllByOrderByDatePublicationDesc();
    List<Publication> findTop5ByOrderByDatePublicationDesc();

    }

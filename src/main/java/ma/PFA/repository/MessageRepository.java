package ma.PFA.repository;

import ma.PFA.entity.Message;
import ma.PFA.entity.Sujet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySujetOrderByDateMessageAsc(Sujet sujet);
}
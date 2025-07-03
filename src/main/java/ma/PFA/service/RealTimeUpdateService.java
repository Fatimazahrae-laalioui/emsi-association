package ma.PFA.service;

import ma.PFA.entity.Commentaire;
import ma.PFA.entity.Publication;
import ma.PFA.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealTimeUpdateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final PublicationRepository publicationRepository;

    @Autowired
    public RealTimeUpdateService(SimpMessagingTemplate messagingTemplate,
                                 PublicationRepository publicationRepository) {
        this.messagingTemplate = messagingTemplate;
        this.publicationRepository = publicationRepository;
    }

    public void notifierNouvellePublication(Publication publication) {
        messagingTemplate.convertAndSend("/topic/publications", publication);
    }

    public void notifierNouveauCommentaire(Commentaire commentaire) {
        messagingTemplate.convertAndSend(
                "/topic/publication." + commentaire.getPublication().getId() + ".commentaires",
                commentaire
        );
    }

    @Scheduled(fixedRate = 30000)
    public void envoyerMisesAJourPeriodiques() {
        List<Publication> publications = publicationRepository.findTop5ByOrderByDatePublicationDesc();
        messagingTemplate.convertAndSend("/topic/publications/recentes", publications);
    }
}

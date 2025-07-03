package ma.PFA.service;

import ma.PFA.entity.Evenement;
import ma.PFA.entity.Inscription;
import ma.PFA.entity.InscriptionStatus;
import ma.PFA.entity.User;
import ma.PFA.repository.EvenementRepository;
import ma.PFA.repository.InscriptionRepository; // ✅ Ajoute cet import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final InscriptionRepository inscriptionRepository; // ✅ Ajouté

    @Autowired
    public EvenementService(EvenementRepository evenementRepository,
                            InscriptionRepository inscriptionRepository) { // ✅ Ajouté
        this.evenementRepository = evenementRepository;
        this.inscriptionRepository = inscriptionRepository; // ✅ Ajouté
    }

    public List<Evenement> getLatestEvents() {
        return evenementRepository.findTop5ByOrderByDateDesc();
    }

    public List<Evenement> getAllEvents() {
        return evenementRepository.findAllByOrderByDateDesc();
    }

    public List<Evenement> getUpcomingEvents() {
        return evenementRepository.findByDateAfterOrderByDateAsc(LocalDate.now());
    }

    public Optional<Evenement> getEventById(Long id) {
        return evenementRepository.findById(id);
    }

    public Evenement saveEvent(Evenement event) {
        return evenementRepository.save(event);
    }

    public Inscription registerUserToEvenement(User user, Evenement evenement) {
        Inscription inscription = new Inscription();
        inscription.setUser(user);
        inscription.setEvenement(evenement);
        inscription.setStatus(InscriptionStatus.CONFIRMED); // ou PENDING selon ta logique métier
        inscription.setLu(false);
        inscription.setDateInscription(LocalDateTime.now());

        return inscriptionRepository.save(inscription);
    }

    public long countEvents() {
        return evenementRepository.count();
    }
}

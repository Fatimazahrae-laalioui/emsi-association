package ma.PFA.service;

import ma.PFA.entity.Inscription;
import ma.PFA.entity.InscriptionStatus;
import ma.PFA.entity.User;
import ma.PFA.entity.Evenement;
import ma.PFA.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;

    @Autowired
    public InscriptionService(InscriptionRepository inscriptionRepository) {
        this.inscriptionRepository = inscriptionRepository;
    }

    public Inscription registerForEvent(User user, Evenement event) {
        Inscription inscription = new Inscription();
        inscription.setUser(user);
        inscription.setEvenement(event);
        inscription.setDateInscription(LocalDateTime.now());
        inscription.setStatus(InscriptionStatus.PENDING);
        return inscriptionRepository.save(inscription);
    }

    public List<Inscription> getUserRegistrations(User user) {
        return inscriptionRepository.findByUser(user);
    }

    public List<Inscription> getEventRegistrations(Evenement event) {
        return inscriptionRepository.findByEvenement(event);
    }

    public boolean isUserRegistered(User user, Evenement event) {
        return inscriptionRepository.existsByUserAndEvenement(user, event);
    }

    public void cancelRegistration(Long registrationId) {
        inscriptionRepository.deleteById(registrationId);
    }
    public void approveInscription(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));
        inscription.setStatus(InscriptionStatus.CONFIRMED);
        inscription.setMessage("Votre demande d'inscription a été approuvée !");
        inscriptionRepository.save(inscription);
    }

    public void rejectInscription(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));
        inscription.setStatus(InscriptionStatus.REJECTED);
        inscription.setMessage("Désolé, votre demande d'inscription a été refusée.");
        inscriptionRepository.save(inscription);
    }

}
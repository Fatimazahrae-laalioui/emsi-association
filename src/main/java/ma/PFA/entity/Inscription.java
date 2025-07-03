package ma.PFA.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String motif;
    private LocalDateTime dateInscription;
    private String message;
    @Column(nullable = false)
    private boolean lu = false;



    @ManyToOne
    private User user;

    @ManyToOne
    private Evenement evenement;


    // Getters / Setters


    @ManyToOne
    @JoinColumn(name = "association_id") // facultatif, pour nommer la colonne
    private Association association;



    @Enumerated(EnumType.STRING)
    private InscriptionStatus status = InscriptionStatus.PENDING;

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
}



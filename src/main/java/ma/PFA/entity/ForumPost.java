package ma.PFA.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @ManyToOne(fetch = FetchType.LAZY)
    private User auteur;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ForumReply> reponses;

    private LocalDateTime date; // Utilisez soit 'date' soit 'dateCreation' mais pas les deux

    // Si vous voulez utiliser setDateCreation() au lieu de setDate()
    public void setDateCreation(LocalDateTime dateCreation) {
        this.date = dateCreation;
    }

    // Optionnel : méthode pour initialiser la date automatiquement
    @PrePersist
    public void prePersist() {
        this.date = LocalDateTime.now();
        if (this.likeCount == null) this.likeCount = 0;
    }
    private String imageUrl;
    // Ajouter ce champ pour les images


    private Integer likeCount = 0;
}
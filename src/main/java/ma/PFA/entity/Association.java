package ma.PFA.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;

    @OneToMany(mappedBy = "association")
    private List<Evenement> evenements;

    @OneToMany(mappedBy = "association")
    private List<Inscription> inscriptions;

    // getters, setters
}

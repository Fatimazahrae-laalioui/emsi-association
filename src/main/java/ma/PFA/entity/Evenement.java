package ma.PFA.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private LocalDate date;
    private String lieu;
    private String imageFilename;
    @Column(name = "nb_places")
    private Integer nbPlaces;


    @ManyToMany
    @JoinTable(
            name = "evenement_participants",
            joinColumns = @JoinColumn(name = "evenement_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();
    @ManyToOne
    private Association association;


}

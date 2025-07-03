package ma.PFA.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;

    @ManyToOne
    private User auteur;

    @ManyToOne
    private Sujet sujet;

    private LocalDateTime dateMessage;

}


package ma.PFA.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class Sujet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @ManyToOne
    @JoinColumn(name = "forum_id")
    private ForumPost forum; // ✅ propriété attendue par Spring

    private LocalDateTime dateCreation;

    // getters et setters
}

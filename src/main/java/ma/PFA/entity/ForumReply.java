package ma.PFA.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class ForumReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @ManyToOne(fetch = FetchType.LAZY)
    private ForumPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User auteur;

    private LocalDateTime date;
}
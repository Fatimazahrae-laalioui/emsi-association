package ma.PFA.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SavedPublication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Publication publication;

    @ManyToOne
    private User user;
}

package ma.PFA.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String imageUrl;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;  // "ROLE_ADMIN" ou "ROLE_STUDENT"

    @Column(nullable = false)
    private String fullName;

    @OneToMany(mappedBy = "user")
    private List<Inscription> inscriptions;

    @Transient // Ce champ ne sera pas persisté en base de données
    private boolean isConnected = false;

    private Integer nbActions = 0;

    // Constructeurs
    public User() {}
}
package ma.PFA.service;

import ma.PFA.entity.User;
import ma.PFA.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String getUserRole(String email) {
        Optional<User> userOpt = findByEmail(email);
        if (userOpt.isPresent()) {
            return userOpt.get().getRole();
        } else {
            throw new RuntimeException("Utilisateur non trouvé avec email : " + email);
            // Ou return null / valeur par défaut selon ta logique
        }
    }


    public User saveUser (User user) {
        return userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public long countByRole(String role) {
        return userRepository.countByRole(role);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    public long countStudents() {
        return userRepository.count();
    }
    public List<User> getMembresActifs() {
        // Exemple : retourne les 5 plus actifs
        return userRepository.findTop5ByOrderByNbActionsDesc();
    }

}

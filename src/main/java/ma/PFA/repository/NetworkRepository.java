package ma.PFA.repository;

import ma.PFA.entity.Network;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkRepository extends JpaRepository<Network, Long> {}
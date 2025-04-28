package repository;


import domain.RsaKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RsaKeyRepository extends JpaRepository<RsaKey, UUID> {
    Optional<RsaKey> findByElectionAuthorityId(UUID electionId);  // Adjust to match your RsaKey entity
}

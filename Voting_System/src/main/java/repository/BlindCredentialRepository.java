package repository;

import domain.BlindCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlindCredentialRepository extends JpaRepository<BlindCredential, UUID> {
    // You can define custom queries if necessary, for example:
    // Optional<BlindCredential> findByVoterId(UUID voterId);
}

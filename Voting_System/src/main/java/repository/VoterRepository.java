package repository;

import domain.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VoterRepository extends JpaRepository<Voter, UUID> {
    Optional<Voter> findByVoterEmail(String voterEmail);
}

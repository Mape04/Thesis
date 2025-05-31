package repository;

import domain.Voter;
import domain.VoterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoterRepository extends JpaRepository<Voter, UUID> {
    Optional<Voter> findByVoterEmail(String voterEmail);
    List<Voter> findByVoterType(VoterType voterType);
    Optional<Voter> findByVoterIdAndIsVerifiedHumanTrue(UUID voterId);
    boolean existsByCnpHash(String cnpHash);


}

package repository;

import domain.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Repository
public interface CandidateRepository  extends JpaRepository<Candidate, UUID> {
    List<Candidate> findByElection_ElectionId(UUID electionId);

    @Query("SELECT c FROM Candidate c WHERE c.election.electionId = :electionId ORDER BY c.nrOfVotes DESC")
    List<Candidate> findTopNCandidatesByElectionId(@Param("electionId") UUID electionId, Pageable pageable);

}

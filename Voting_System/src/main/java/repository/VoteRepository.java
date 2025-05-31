package repository;

import domain.Vote;
import dto.analytics.VoteLightDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import projection.VoteSummary;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {

    @Query("SELECT COUNT(v) FROM Vote v JOIN v.selectedCandidates c WHERE c.candidateId = :candidateId")
    long countVotesByCandidate(@Param("candidateId") UUID candidateId);
    List<VoteSummary> findByBallot_Election_ElectionId(UUID electionId);

    @Query("SELECT new dto.analytics.VoteLightDTO(v.timestamp, c, b.region) " +
            "FROM Vote v JOIN v.selectedCandidates c JOIN v.ballot b " +
            "WHERE b.election.electionId = :electionId")
    List<VoteLightDTO> getVoteLightByElection(@Param("electionId") UUID electionId);


}

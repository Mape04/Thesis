package repository;

import domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {

    @Query("SELECT COUNT(v) FROM Vote v JOIN v.selectedCandidates c WHERE c.candidateId = :candidateId")
    long countVotesByCandidate(@Param("candidateId") UUID candidateId);
}

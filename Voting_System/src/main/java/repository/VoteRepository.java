package repository;

import domain.Election;
import domain.Vote;
import domain.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    long countByVoterAndBallot_Election(Voter voter, Election election);
}

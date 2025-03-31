package service;

import domain.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.VoteRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoteService {
    private final VoteRepository voteRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    // Create or update a Vote
    public Vote saveVote(Vote Vote) {
        return voteRepository.save(Vote);
    }

    // Get all Votes
    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    // Get a Vote by ID
    public Optional<Vote> getVoterById(UUID id) {
        return voteRepository.findById(id);
    }

    // Delete a Vote by ID
    public void deleteVote(UUID id) {
        voteRepository.deleteById(id);
    }
}

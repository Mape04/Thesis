package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.BallotRepository;

import java.util.UUID;

@Service
public class BallotService {
    private BallotRepository ballotRepository;

    @Autowired
    private BallotService(BallotRepository ballotRepository) {
        this.ballotRepository = ballotRepository;
    }

    public long countByElection_ElectionId(UUID electionId) {
        return ballotRepository.countByElection_ElectionId(electionId);
    }
}

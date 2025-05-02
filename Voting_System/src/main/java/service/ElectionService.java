package service;

import domain.Election;
import domain.ElectionAuthority;
import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import repository.ElectionAuthorityRepository;
import repository.ElectionRepository;
import repository.VoterRepository;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final ElectionAuthorityRepository electionAuthorityRepository;

    @Autowired
    public ElectionService(ElectionRepository electionRepository, ElectionAuthorityRepository electionAuthorityRepository) {
        this.electionRepository = electionRepository;
        this.electionAuthorityRepository = electionAuthorityRepository;
    }

    public Election createElection(UUID authorityId, Election electionData) {
        ElectionAuthority authority = electionAuthorityRepository.findById(authorityId)
                .orElseThrow(() -> new RuntimeException("Authority not found."));

        Election newElection = new Election();
        newElection.setElectionName(electionData.getElectionName());
        newElection.setStartDate(electionData.getStartDate());
        newElection.setEndDate(electionData.getEndDate());
        newElection.setElectionVotes(0);
        newElection.setElectionDescription(electionData.getElectionDescription());
        newElection.setNrVotesPerVoter(electionData.getNrVotesPerVoter());
        newElection.setElectionAuthority(authority);

        // 🔐 Password hashing (only if provided)
        String rawPassword = electionData.getElectionPassword();
        if (rawPassword != null && !rawPassword.isBlank()) {
            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            newElection.setElectionPassword(hashedPassword);
        } else {
            newElection.setElectionPassword(null); // allow no password
        }

        return electionRepository.save(newElection);
    }

    public Election updateElection(UUID electionId, Election updatedData) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        if (LocalDateTime.now().isAfter(election.getEndDate())) {
            throw new IllegalStateException("Cannot edit a finished election.");
        }

        election.setElectionName(updatedData.getElectionName());
        election.setElectionDescription(updatedData.getElectionDescription());
        election.setStartDate(updatedData.getStartDate());
        election.setEndDate(updatedData.getEndDate());
        election.setNrVotesPerVoter(updatedData.getNrVotesPerVoter());
        election.setElectionPassword(updatedData.getElectionPassword());

        return electionRepository.save(election);
    }



    // Create or update an election
    public Election saveElection(Election election) {
        return electionRepository.save(election);
    }

    // Get all elections
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    // Get an election by ID
    public Optional<Election> getElectionById(UUID id) {
        return electionRepository.findById(id);
    }

    // Delete an election by ID
    public void deleteElection(UUID id) {
        electionRepository.deleteById(id);
    }

}

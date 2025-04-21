package service;

import domain.Election;
import domain.ElectionAuthority;
import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ElectionAuthorityRepository;
import repository.ElectionRepository;
import repository.VoterRepository;

import java.security.GeneralSecurityException;
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
                .orElseThrow(() -> new IllegalArgumentException("Election Authority not found with ID: " + authorityId));

        electionData.setElectionAuthority(authority);
        return electionRepository.save(electionData);
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

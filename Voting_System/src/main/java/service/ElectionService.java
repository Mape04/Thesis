package service;

import domain.Election;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ElectionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;

    @Autowired
    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
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

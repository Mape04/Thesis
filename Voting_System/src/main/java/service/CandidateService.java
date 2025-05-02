package service;

import domain.Candidate;
import dto.CandidateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.CandidateRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    // Create or update a Candidate
    public Candidate saveCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    // Get all Candidates
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    // Get a Candidate by ID
    public Optional<Candidate> getCandidateById(UUID id) {
        return candidateRepository.findById(id);
    }

    // Delete a Candidate by ID
    public void deleteCandidate(UUID id) {
        candidateRepository.deleteById(id);
    }

    public List<Candidate> findByElection_ElectionId(UUID electionId) {
        return candidateRepository.findByElection_ElectionId(electionId);
    }

    public Candidate updateCandidate(UUID id, CandidateDTO dto) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        candidate.setCandidateName(dto.getCandidateName());
        candidate.setCandidateParty(dto.getCandidateParty());

        return candidateRepository.save(candidate);
    }

}

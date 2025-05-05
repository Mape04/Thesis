package service;

import domain.Candidate;
import dto.CandidateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import repository.CandidateRepository;
import utils.DTOUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<Candidate> getTopNCandidates(UUID electionId, int n) {
        return candidateRepository.findTopNCandidatesByElectionId(electionId, PageRequest.of(0, n));
    }


}

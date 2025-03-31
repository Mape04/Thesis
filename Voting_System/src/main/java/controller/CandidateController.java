package controller;

import domain.Candidate;
import domain.Election;
import domain.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.CandidateRepository;
import service.CandidateService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/candidates")
public class CandidateController {
    private final CandidateService candidateService;

    @Autowired
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    public List<Candidate> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    // Create a new candidate
    @PostMapping
    public ResponseEntity<Candidate> createElection(@RequestBody Candidate candidate) {
        Candidate savedCandidate = candidateService.saveCandidate(candidate);
        return ResponseEntity.ok(savedCandidate);
    }

    // Get a specific Candidate by ID
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable UUID id) {
        Optional<Candidate> Candidate = candidateService.getCandidateById(id);
        return Candidate.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a Candidate by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable UUID id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

}

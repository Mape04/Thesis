package controller;

import domain.Candidate;
import domain.Election;
import domain.Election;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CandidateService;
import service.ElectionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    private final ElectionService electionService;
    private final CandidateService candidateService;

    @Autowired
    public ElectionController(ElectionService electionService, CandidateService candidateService) {
        this.electionService = electionService;
        this.candidateService = candidateService;
    }

    // Create a new election
    @PostMapping
    public ResponseEntity<Election> createElection(@RequestBody Election election) {
        Election savedElection = electionService.saveElection(election);
        return ResponseEntity.ok(savedElection);
    }

    @GetMapping
    public List<Election> getAllElections() {
        return electionService.getAllElections();
    }

    // Get a specific Election by ID
    @GetMapping("/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable UUID id) {
        Optional<Election> Election = electionService.getElectionById(id);
        return Election.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a Election by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteElection(@PathVariable UUID id) {
        electionService.deleteElection(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{electionId}/candidates")
    public ResponseEntity<List<Candidate>> getCandidatesByElection(@PathVariable UUID electionId) {
        List<Candidate> candidates = candidateService.findByElection_ElectionId(electionId);

        if (candidates.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content if no candidates found
        }

        return ResponseEntity.ok(candidates); // 200 OK with the list of candidates
    }
}

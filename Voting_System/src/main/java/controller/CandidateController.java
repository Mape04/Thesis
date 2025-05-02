package controller;

import domain.Election;
import dto.CandidateDTO;
import domain.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CandidateService;
import service.ElectionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static utils.DTOUtils.toCandidateDTO;

@RestController
@RequestMapping("api/candidates")
public class CandidateController {

    private final CandidateService candidateService;
    private final ElectionService electionService;

    @Autowired
    public CandidateController(CandidateService candidateService, ElectionService electionService) {
        this.candidateService = candidateService;
        this.electionService = electionService;
    }

    // Create a new candidate
    @PostMapping
    public ResponseEntity<?> createCandidate(@RequestBody CandidateDTO candidateDTO) {
        Optional<Election> election = electionService.getElectionById(candidateDTO.getCandidateElectionId());
        if (election.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Election not found"));
        }

        Candidate candidate = utils.DTOUtils.toCandidate(candidateDTO, election.get());
        Candidate savedCandidate = candidateService.saveCandidate(candidate);
        CandidateDTO response = toCandidateDTO(savedCandidate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateDTO> updateCandidate(
            @PathVariable UUID id,
            @RequestBody CandidateDTO dto) {

        Candidate updated = candidateService.updateCandidate(id, dto);
        return ResponseEntity.ok(toCandidateDTO(updated));
    }




    @GetMapping
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        List<CandidateDTO> candidateDTOs = utils.DTOUtils.toCandidateDTOList(candidates);
        return ResponseEntity.ok(candidateDTOs);
    }


    // Get a specific Candidate by ID
    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> getCandidateById(@PathVariable UUID id) {
        Optional<Candidate> candidate = candidateService.getCandidateById(id);
        return candidate.map(c -> ResponseEntity.ok(toCandidateDTO(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a Candidate by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable UUID id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }
}

package controller;

import domain.Candidate;
import domain.Election;
import dto.CandidateDTO;
import dto.ElectionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CandidateService;
import service.ElectionService;
import utils.DTOUtils;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;
    private final CandidateService candidateService;


    @PostMapping("/{authorityId}")
    public ResponseEntity<ElectionDTO> createElection(
            @PathVariable UUID authorityId,
            @RequestBody Election electionData) {

        Election createdElection = electionService.createElection(authorityId, electionData);
        ElectionDTO response = DTOUtils.toElectionDTO(createdElection);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ElectionDTO>> getAllElections() {
        List<ElectionDTO> electionDTOs = electionService.getAllElections()
                .stream()
                .map(DTOUtils::toElectionDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(electionDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElectionDTO> getElectionById(@PathVariable UUID id) {
        Optional<Election> election = electionService.getElectionById(id);
        return election.map(value -> ResponseEntity.ok(DTOUtils.toElectionDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteElection(@PathVariable UUID id) {
        electionService.deleteElection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{electionId}/candidates")
    public ResponseEntity<List<CandidateDTO>> getCandidatesByElection(@PathVariable UUID electionId) {
        List<Candidate> candidates = candidateService.findByElection_ElectionId(electionId);

        if (candidates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<CandidateDTO> candidateDTOs = candidates.stream()
                .map(DTOUtils::toCandidateDTO)  // assuming you’ve got a CandidateDTO and a mapper
                .collect(Collectors.toList());

        return ResponseEntity.ok(candidateDTOs);
    }
}

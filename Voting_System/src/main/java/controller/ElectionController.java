package controller;

import domain.Candidate;
import domain.Election;
import dto.CandidateDTO;
import dto.ElectionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import service.BallotService;
import service.CandidateService;
import service.ElectionService;
import service.VoteService;
import utils.DTOUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final VoteService voteService;
    private final BallotService ballotService;


    @PostMapping("/{authorityId}")
    public ResponseEntity<ElectionDTO> createElection(
            @PathVariable UUID authorityId,
            @RequestBody ElectionDTO electionData) {

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
                .map(DTOUtils::toCandidateDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(candidateDTOs);
    }

    @GetMapping("{electionId}/live-results")
    public ResponseEntity<Map<String, Long>> getLiveResults(@PathVariable UUID electionId) {
        Election election = electionService.getElectionById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        List<Candidate> candidates = candidateService.findByElection_ElectionId(electionId);

        Map<String, Long> results = new HashMap<>();
        for (Candidate candidate : candidates) {
            long votes = voteService.countVotesByCandidate(candidate.getCandidateId());
            results.put(candidate.getCandidateName(), votes);
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/{electionId}/final-results")
    public ResponseEntity<Map<UUID, Integer>> getFinalResults(@PathVariable UUID electionId) {
        try {
            Map<UUID, Integer> results = voteService.decryptAndTallyVotes(electionId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("{electionId}/vote-count")
    public ResponseEntity<Long> getTotalVotes(@PathVariable UUID electionId) {
        long totalVotes = ballotService.countByElection_ElectionId(electionId);
        return ResponseEntity.ok(totalVotes);
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyElectionPassword(@RequestBody Map<String, String> payload) {
        UUID electionId = UUID.fromString(payload.get("electionId"));
        String providedPassword = payload.get("password");

        Optional<Election> election = electionService.getElectionById(electionId);

        if (election.get().getElectionPassword() != null) {
            boolean matches = BCrypt.checkpw(providedPassword, election.get().getElectionPassword());
            if (!matches) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Invalid election password."));
            }
        }

        return ResponseEntity.ok(DTOUtils.toElectionDTO(election.orElse(null)));
    }

    @PutMapping("/{electionId}")
    public ResponseEntity<ElectionDTO> updateElection(
            @PathVariable UUID electionId,
            @RequestBody Election updatedData) {

        Election updated = electionService.updateElection(electionId, updatedData);
        return ResponseEntity.ok(DTOUtils.toElectionDTO(updated));
    }


}

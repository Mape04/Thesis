package controller;

import domain.Election;
import dto.VoterDTO;
import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ElectionService;
import service.VoteService;
import service.VoterService;
import utils.DTOUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/voters")  // Base URL for all the Voter-related endpoints
public class VoterController {

    private final VoterService voterService;
    private final ElectionService electionService;
    private final VoteService voteService;


    @Autowired
    public VoterController(VoterService voterService, ElectionService electionService, VoteService voteService) {
        this.voterService = voterService;
        this.electionService = electionService;
        this.voteService = voteService;
    }

    // Create or update a voter
    @PostMapping
    public ResponseEntity<VoterDTO> createVoter(@RequestBody VoterDTO voterDTO) {
        System.out.println(voterDTO);
        Voter voter = DTOUtils.toVoter(voterDTO);  // Convert DTO to entity
        Voter savedVoter = voterService.saveVoter(voter);
        VoterDTO savedVoterDTO = DTOUtils.toVoterDTO(savedVoter);  // Convert saved entity back to DTO
        return new ResponseEntity<>(savedVoterDTO, HttpStatus.CREATED);
    }

    // Get all voters
    @GetMapping
    public List<VoterDTO> getAllVoters() {
        List<Voter> voters = voterService.getAllVoters();
        return voters.stream()
                .map(DTOUtils::toVoterDTO)  // Convert entities to DTOs
                .collect(Collectors.toList());
    }

    // Get a specific voter by ID
    @GetMapping("/{id}")
    public ResponseEntity<VoterDTO> getVoterById(@PathVariable UUID id) {
        Optional<Voter> voter = voterService.getVoterById(id);
        return voter.map(v -> ResponseEntity.ok(DTOUtils.toVoterDTO(v)))  // Convert entity to DTO
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a voter by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoter(@PathVariable UUID id) {
        voterService.deleteVoter(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{voterId}/elections/{electionId}/voted")
    public ResponseEntity<Boolean> hasVoterVoted(
            @PathVariable UUID voterId,
            @PathVariable UUID electionId) {

        Voter voter = voterService.getVoterById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));
        Election election = electionService.getElectionById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        long votes = voteService.countByVoterAndBallot_Election(voter, election);
        return ResponseEntity.ok(votes > 0);
    }


}

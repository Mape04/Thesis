package controller;

import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.VoterService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/voters")  // Base URL for all the Voter-related endpoints
public class VoterController {

    private final VoterService voterService;

    @Autowired
    public VoterController(VoterService voterService) {
        this.voterService = voterService;
    }

    // Create or update a voter
    @PostMapping
    public ResponseEntity<Voter> createVoter(@RequestBody Voter voter) {
        System.out.println(voter);
        Voter savedVoter = voterService.saveVoter(voter);
        return new ResponseEntity<>(savedVoter, HttpStatus.CREATED);
    }

    // Get all voters
    @GetMapping
    public List<Voter> getAllVoters() {
        return voterService.getAllVoters();
    }

    // Get a specific voter by ID
    @GetMapping("/{id}")
    public ResponseEntity<Voter> getVoterById(@PathVariable UUID id) {
        Optional<Voter> voter = voterService.getVoterById(id);
        return voter.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a voter by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoter(@PathVariable UUID id) {
        voterService.deleteVoter(id);
        return ResponseEntity.noContent().build();
    }
}

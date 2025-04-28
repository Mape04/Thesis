package controller;

import domain.BlindCredential;
import domain.Election;
import domain.ElectionAuthority;
import domain.Voter;
import dto.BlindCredentialDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.BlindCredentialRepository;
import service.ElectionService;
import service.VoterService;

@RestController
@RequestMapping("/api/blind-credential")
@RequiredArgsConstructor
public class BlindCredentialController {

    private final BlindCredentialRepository blindCredentialRepository;
    private final VoterService voterService;
    private final ElectionService electionService;

    @PostMapping("/save")
    public ResponseEntity<?> saveBlindCredential(@RequestBody BlindCredentialDTO request) {
        try {
            // 🔥 Fetch voter
            Voter voter = voterService.getVoterById(request.getVoterId())
                    .orElseThrow(() -> new IllegalArgumentException("Voter not found"));

            // 🔥 Fetch election
            Election election = electionService.getElectionById(request.getElectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Election not found"));

            // 🔥 Fetch election authority
            ElectionAuthority authority = election.getElectionAuthority();
            if (authority == null) {
                throw new IllegalArgumentException("Election has no ElectionAuthority linked.");
            }

            // 🔥 Create new BlindCredential
            BlindCredential blindCredential = new BlindCredential();
            blindCredential.setVoter(voter);
            blindCredential.setElection(election);
            blindCredential.setElectionAuthority(authority);
            blindCredential.setSignedToken(request.getVoterToken());

            blindCredentialRepository.save(blindCredential);

            return ResponseEntity.ok("BlindCredential saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving BlindCredential: " + e.getMessage());
        }
    }
}

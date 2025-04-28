package controller;

import domain.Voter;
import dto.VoterLoginDTO;
import dto.VoterRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import service.ElectionAuthorityService;
import service.EncryptionService;
import service.VoterService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")  // Allow React dev server access
public class AuthController {

    private final VoterService voterService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    @Autowired
    public AuthController(VoterService voterService, BCryptPasswordEncoder passwordEncoder, EncryptionService encryptionService) {
        this.voterService = voterService;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
    }

    // Registration
    @PostMapping("/register")
    public ResponseEntity<?> registerVoter(@RequestBody VoterRegistrationDTO voterDTO) {
        if (voterDTO.getVoterEmail() == null || voterDTO.getVoterPassword() == null || voterDTO.getVoterName() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name, email, and password must not be null"));
        }

        // Prevent duplicate registration
        if (voterService.getVoterByEmail(voterDTO.getVoterEmail()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already in use"));
        }

        Voter voter = new Voter();
        voter.setVoterName(voterDTO.getVoterName());
        voter.setVoterEmail(voterDTO.getVoterEmail());
        voter.setVoterPassword(voterDTO.getVoterPassword());
        voter.setVoterIsRegistered(false);

        Voter savedVoter = voterService.saveVoter(voter);

        return ResponseEntity.ok(Map.of("message", "Voter registered successfully", "voterId", savedVoter.getVoterId()));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> loginVoter(@RequestBody VoterLoginDTO loginDTO) {
        if (loginDTO.getVoterEmail() == null || loginDTO.getVoterPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password must not be null"));
        }

        Optional<Voter> optionalVoter = voterService.getVoterByEmail(loginDTO.getVoterEmail());

        if (optionalVoter.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Voter not found"));
        }

        Voter storedVoter = optionalVoter.get();

        // Check if the password matches the hashed version stored in the database
        if (!passwordEncoder.matches(loginDTO.getVoterPassword(), storedVoter.getVoterPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        String voterToken = encryptionService.generateVoterToken(storedVoter.getVoterId());

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "voterId", storedVoter.getVoterId(),
                "voterToken", voterToken
        ));
    }


}

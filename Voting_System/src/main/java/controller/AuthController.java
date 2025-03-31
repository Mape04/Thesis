package controller;

import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import service.VoterService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final VoterService voterService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(VoterService voterService, BCryptPasswordEncoder passwordEncoder) {
        this.voterService = voterService;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new voter
    @PostMapping("/register")
    public ResponseEntity<String> registerVoter(@RequestBody Voter voter) {
        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(voter.getVoterHashedPassword());
        voter.setVoterHashedPassword(hashedPassword);

        // Save voter to database
        voterService.saveVoter(voter);

        return ResponseEntity.ok("Voter registered successfully");
    }

    // Login a voter (for example, checking hashed password)
    @PostMapping("/login")
    public ResponseEntity<?> loginVoter(@RequestBody Voter loginRequest) {
        Optional<Voter> optionalVoter = voterService.getVoterByEmail(loginRequest.getVoterEmail());

        if (optionalVoter.isPresent()) {
            Voter storedVoter = optionalVoter.get();
            if (passwordEncoder.matches(loginRequest.getVoterHashedPassword(), storedVoter.getVoterHashedPassword())) {
                return ResponseEntity.ok().body(Map.of("message", "Login successful"));
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Voter not found"));
        }
    }
}

package controller;

import dto.VoterDTO;
import domain.Voter;
import dto.VoterVerificationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import service.ElectionService;
import service.VoteService;
import service.VoterService;
import utils.DTOUtils;
import validators.VoterValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/voters")
public class VoterController {

    private final VoterService voterService;
    private final ElectionService electionService;
    private final VoteService voteService;


    @Autowired
    public VoterController(VoterService voterService, ElectionService electionService, VoteService voteService, VoterValidator voterValidator) {
        this.voterService = voterService;
        this.electionService = electionService;
        this.voteService = voteService;
    }

    // Create a voter
    @PostMapping
    public ResponseEntity<VoterDTO> createVoter(@RequestBody VoterDTO voterDTO) {
        System.out.println(voterDTO);
        Voter voter = DTOUtils.toVoter(voterDTO);  // Convert DTO to entity
        Voter savedVoter = voterService.saveVoter(voter);
        VoterDTO savedVoterDTO = DTOUtils.toVoterDTO(savedVoter);  // Convert saved entity back to DTO
        return new ResponseEntity<>(savedVoterDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoterDTO> updateVoter(
            @PathVariable UUID id,
            @RequestParam("voterName") String name,
            @RequestParam("voterEmail") String email,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Voter updatedVoter = voterService.updateVoterDetails(id, name, email, file);
        VoterDTO responseDTO = DTOUtils.toVoterDTO(updatedVoter);
        return ResponseEntity.ok(responseDTO);
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

    @GetMapping("{voterId}/elections/{electionId}/has-token-been-used")
    public ResponseEntity<Boolean> hasTokenBeenUsed(
            @PathVariable UUID voterId,
            @PathVariable UUID electionId) {

        Voter voter = voterService.getVoterById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        boolean tokenUsed = voter.getBlindCredentialSet().stream()
                .anyMatch(cred -> cred.getElection().getElectionId().equals(electionId) && cred.isUsed());

        return ResponseEntity.ok(tokenUsed);
    }


    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadProfileImage(@PathVariable UUID id,
                                                @RequestParam("file") MultipartFile file) {
        try {
            String imagePath = voterService.saveProfileImage(id, file);
            return ResponseEntity.ok(Map.of("profileImagePath", imagePath));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getVoterImage(@PathVariable UUID id) {
        Optional<Voter> optionalVoter = voterService.getVoterById(id);

        if (optionalVoter.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Voter voter = optionalVoter.get();
        String storedPath = voter.getProfileImagePath();

        try {
            File imageFile;

            if (storedPath != null && !storedPath.isEmpty()) {
                // Stored path is relative to /uploads
                imageFile = new File("src/main/resources/uploads" + storedPath);

                if (!imageFile.exists()) {
                    System.out.println("⚠Profile image not found, using default.");
                    imageFile = new File("src/main/resources/static/default-user.png");
                }
            } else {
                System.out.println("No image set, using default.");
                imageFile = new File("src/main/resources/static/default-user.png");
            }

            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getMediaType(imageFile.getName()));

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error reading image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private MediaType getMediaType(String filename) {
        if (filename.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (filename.endsWith(".gif")) return MediaType.IMAGE_GIF;
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    @PostMapping("/{voterId}/verify-human")
    public ResponseEntity<?> verifyHuman(
            @PathVariable UUID voterId,
            @RequestBody VoterVerificationRequestDTO request) {

        if (request.getSsn() == null || request.getSsn().length() != 13) {
            return ResponseEntity.badRequest().body(Map.of("error", "SSN (CNP) is required and must be 13 digits."));
        }

        try {
            Voter voter = voterService.getVoterById(voterId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voter not found"));

            voter.setBirthdate(request.getBirthdate());
            voter.setRegion(request.getRegion());

            // Pass the enriched entity and CNP to service
            voterService.verifyHuman(voter, request.getSsn());

            return ResponseEntity.ok(Map.of("status", "verified"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Verification failed: " + e.getMessage()));
        }
    }



}

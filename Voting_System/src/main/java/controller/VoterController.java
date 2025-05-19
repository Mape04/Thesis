package controller;

import domain.Election;
import dto.VoterDTO;
import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.ElectionService;
import service.VoteService;
import service.VoterService;
import utils.DTOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.MediaTypeFactory.*;

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
                    System.out.println("⚠️ Profile image not found, using default.");
                    imageFile = new File("src/main/resources/static/default-user.png");
                }
            } else {
                System.out.println("ℹ️ No image set, using default.");
                imageFile = new File("src/main/resources/static/default-user.png");
            }

            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getMediaType(imageFile.getName()));

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ Error reading image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private MediaType getMediaType(String filename) {
        if (filename.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (filename.endsWith(".gif")) return MediaType.IMAGE_GIF;
        return MediaType.APPLICATION_OCTET_STREAM;
    }




}

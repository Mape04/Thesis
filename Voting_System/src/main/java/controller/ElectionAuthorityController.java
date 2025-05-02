package controller;

import domain.Election;
import domain.Voter;
import dto.ElectionAuthorityDTO;
import domain.ElectionAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;
import service.ElectionAuthorityService;
import service.ElectionService;
import service.VoterService;
import utils.DTOUtils;

import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/election-authorities")
@RequiredArgsConstructor
public class ElectionAuthorityController {

    private final ElectionAuthorityService electionAuthorityService;
    private final VoterService voterService;
    private final ElectionService electionService;

    // Create a new Election Authority
    @PostMapping
    public ResponseEntity<ElectionAuthorityDTO> createElectionAuthority(@RequestBody ElectionAuthorityDTO electionAuthorityDTO) throws Exception {
        ElectionAuthority electionAuthority = DTOUtils.toElectionAuthority(electionAuthorityDTO);  // Convert DTO to entity
        ElectionAuthority createdAuthority = electionAuthorityService.createElectionAuthority(electionAuthority);
        ElectionAuthorityDTO createdAuthorityDTO = DTOUtils.toElectionAuthorityDTO(createdAuthority);  // Convert entity back to DTO
        return ResponseEntity.ok(createdAuthorityDTO);
    }

    // Get all Election Authorities
    @GetMapping
    public ResponseEntity<List<ElectionAuthorityDTO>> getAllElectionAuthorities() {
        List<ElectionAuthority> authorities = electionAuthorityService.getAllElectionAuthorities();
        List<ElectionAuthorityDTO> authoritiesDTO = authorities.stream()
                .map(DTOUtils::toElectionAuthorityDTO)  // Convert entities to DTOs
                .collect(Collectors.toList());
        return ResponseEntity.ok(authoritiesDTO);
    }

    // Get a specific Election Authority by ID
    @GetMapping("/{id}")
    public ResponseEntity<ElectionAuthorityDTO> getElectionAuthorityById(@PathVariable UUID id) {
        ElectionAuthority authority = electionAuthorityService.getElectionAuthorityById(id);
        if (authority != null) {
            ElectionAuthorityDTO authorityDTO = DTOUtils.toElectionAuthorityDTO(authority);  // Convert entity to DTO
            return ResponseEntity.ok(authorityDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteElectionAuthorityById(@PathVariable UUID id) {
        electionAuthorityService.deleteElectionAuthority(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/match")
    public ResponseEntity<Boolean> isVoterAuthority(
            @RequestParam UUID voterId,
            @RequestParam UUID electionId
    ) {
        Voter voter = voterService.getVoterById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        Election election = electionService.getElectionById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        return ResponseEntity.ok(
                election.getElectionAuthority().getAuthorityEmail().equalsIgnoreCase(voter.getVoterEmail())
        );
    }

}

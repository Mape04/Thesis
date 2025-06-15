package controller;

import domain.*;
import dto.BlindCredentialDTO;
import dto.BlindedMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.RsaKeyRepository;
import service.BlindCredentialService;
import service.ElectionService;
import service.EncryptionService;
import service.VoterService;
import utils.DTOUtils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/encryption")
public class EncryptionController {
    private final BlindCredentialService blindCredentialService;
    private EncryptionService encryptionService;  // Service responsible for signing the blinded message
    private RsaKeyRepository rsaKeyRepository;  // Repository to fetch RSA keys
    private ElectionService electionService;
    private VoterService voterService;
    @Autowired
    private DTOUtils dtoUtils;

    @Autowired
    public EncryptionController(RsaKeyRepository rsaKeyRepository, EncryptionService encryptionService, ElectionService electionService, VoterService voterService, BlindCredentialService blindCredentialService) {
        this.rsaKeyRepository = rsaKeyRepository;
        this.encryptionService = encryptionService;
        this.electionService = electionService;
        this.voterService = voterService;
        this.blindCredentialService = blindCredentialService;
    }

    @PostMapping("/sign-blinded-message")
    public ResponseEntity<String> signBlindedMessage(@RequestBody BlindedMessageDTO request) {
        try {
            BigInteger blindedMessage = new BigInteger(request.getBlindedMessage());

            String signedBlindedMessage = encryptionService.signBlindedMessage(blindedMessage, request.getElectionId());

            //Save BlindCredential from BlindedMessageDTO
            BlindCredential blindCredential = dtoUtils.toBlindCredential(request, signedBlindedMessage);
            blindCredentialService.save(blindCredential);

            return ResponseEntity.ok(signedBlindedMessage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error signing blinded message: " + e.getMessage());
        }
    }





    // Helper method to convert the blinded candidate IDs to byte[] (can be changed depending on how you want to represent them)
    private byte[] convertToByteArray(long[] blindedCandidateIds) {
        // Convert long[] to byte[] for signing
        // You can implement your own way of converting candidate IDs to bytes (e.g., concatenating them)
        String candidateIdsString = String.join(",", String.valueOf(blindedCandidateIds));
        return candidateIdsString.getBytes();
    }


    @GetMapping("/public-key/{electionId}")
    public ResponseEntity<?> getPublicKey(@PathVariable UUID electionId) {
        try {
            Election election = electionService.getElectionById(electionId)
                    .orElseThrow(() -> new RuntimeException("Election not found"));

            ElectionAuthority authority = election.getElectionAuthority();
            if (authority == null || authority.getRsaKeyId() == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Election Authority RSA Key not found"));
            }

            RsaKey rsaKey = authority.getRsaKeyId();

            Map<String, String> publicKeyData = encryptionService.extractPublicKeyComponents(rsaKey);

            return ResponseEntity.ok(publicKeyData);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error: " + e.getMessage()));
        }
    }


}

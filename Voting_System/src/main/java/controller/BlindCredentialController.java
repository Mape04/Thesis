package controller;

import domain.BlindCredential;
import dto.BlindCredentialDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.BlindCredentialRepository;
import utils.DTOUtils;


@RestController
@RequestMapping("/api/blind-credential")
@RequiredArgsConstructor
public class BlindCredentialController {
    @Autowired
    private DTOUtils dtoUtils;
    private final BlindCredentialRepository blindCredentialRepository;

    @PostMapping("/save")
    public ResponseEntity<?> saveBlindCredential(@RequestBody BlindCredentialDTO request) {
        try {
            //Create new BlindCredential
            BlindCredential blindCredential = dtoUtils.toBlindCredential(request);

            blindCredentialRepository.save(blindCredential);

            return ResponseEntity.ok("BlindCredential saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving BlindCredential: " + e.getMessage());
        }
    }
}

package service;

import domain.Election;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import repository.VoterRepository;
import validators.VoterValidator;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoterService {

    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;
    private final VoterValidator voterValidator;

    private final Path rootPath = Paths.get("src/main/resources/uploads/images");

    @Autowired
    public VoterService(VoterRepository voterRepository, VoterValidator voterValidator) {
        this.voterRepository = voterRepository;
        this.voterValidator = voterValidator;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Initialize BCrypt
    }

    // Create or update a voter
    public Voter saveVoter(Voter voter) {
        //Validating voter fields before submitting
        voterValidator.validate(voter);
        voter.setVoterPassword(passwordEncoder.encode(voter.getVoterPassword()));
        return voterRepository.save(voter);
    }

    // Get all voters
    public List<Voter> getAllVoters() {
        return voterRepository.findAll();
    }

    // Get a voter by ID
    public Optional<Voter> getVoterById(UUID id) {
        return voterRepository.findById(id);
    }

    // Delete a voter by ID
    public void deleteVoter(UUID id) {
        voterRepository.deleteById(id);
    }

    public Optional<Voter> getVoterByEmail(String voterEmail) {
        return voterRepository.findByVoterEmail(voterEmail);
    }

    public String saveProfileImage(UUID voterId, MultipartFile file) throws IOException {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        // Create folder if not exist
        Files.createDirectories(rootPath);

        String fileName = voterId + "_" + file.getOriginalFilename();
        Path filePath = rootPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = "/images/" + fileName;
        voter.setProfileImagePath(relativePath);
        voterRepository.save(voter);

        return relativePath;
    }

    public Voter updateVoterDetails(UUID id, String name, String email, MultipartFile file) throws IOException {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        voter.setVoterName(name);
        voter.setVoterEmail(email);

        if (file != null && !file.isEmpty()) {
            String imagePath = saveProfileImage(id, file);
            voter.setProfileImagePath(imagePath);
        }

        return voterRepository.save(voter);
    }

    public void verifyHuman(Voter voter, String cnp) {
        voterValidator.checkCNP(cnp);
        voterValidator.checkBirthDate(voter.getBirthdate());
        voterValidator.checkRegion(voter.getRegion());

        String hashedCnp = hashCNP(cnp);
        if (voterRepository.existsByCnpHash(hashedCnp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This CNP is already used.");
        }

        voter.setCnpHash(hashedCnp);
        voter.setVerifiedHuman(true);
        voterRepository.save(voter);
    }


    private String hashCNP(String cnp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(cnp.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }


}

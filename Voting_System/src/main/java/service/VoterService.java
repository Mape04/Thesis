package service;

import domain.Election;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repository.VoterRepository;
import validators.VoterValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

}

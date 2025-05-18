package service;

import domain.Election;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import domain.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.VoterRepository;
import validators.VoterValidator;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoterService {

    private final VoterRepository voterRepository;

    private final PasswordEncoder passwordEncoder;
    private final VoterValidator voterValidator;

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

}

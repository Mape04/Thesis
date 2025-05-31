package validators;

import domain.Voter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import repository.VoterRepository;

@Component
public class VoterValidator implements Validator<Voter> {
    private final VoterRepository voterRepository;

    public VoterValidator(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    public void validateEmail(Voter voter) {
        if(voter.getVoterEmail() == null || voter.getVoterEmail().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required.");
        }

        if (!voter.getVoterEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format.");
        }

        if (voterRepository.findByVoterEmail(voter.getVoterEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists.");
        }
    }

    public void validateName(Voter voter) {
        if(voter.getVoterName() == null || voter.getVoterName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty.");
        }
    }

    public void validatePassword(Voter voter){
        if(voter.getVoterPassword() == null || voter.getVoterPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty.");
        }

        if (voter.getVoterPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters: one uppercase, lowercase, digit, special character.");
        }
    }

    public void checkCNP(String cnp) {
        if (cnp == null || !cnp.matches("\\d{13}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNP must be 13 digits.");
        }

        int[] control = {2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9};
        int sum = 0;

        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cnp.charAt(i)) * control[i];
        }

        int checksum = sum % 11;
        if (checksum == 10) checksum = 1;

        int lastDigit = Character.getNumericValue(cnp.charAt(12));
        if (lastDigit != checksum) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CNP checksum.");
        }
    }


    @Override
    public void validate(Voter voter) {
        if(voter == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voter cannot be null");

        validateEmail(voter);
        validateName(voter);
        validatePassword(voter);

    }
}


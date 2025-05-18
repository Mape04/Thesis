package validators;

import domain.Election;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import repository.ElectionRepository;

import java.time.LocalDateTime;

@Component
public class ElectionValidator implements Validator<Election> {
    public void validateName(Election election) {
        if (election.getElectionName() == null || election.getElectionName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Election name is required.");
        }
    }

    public void validateDates(Election election) {
        if (election.getStartDate() == null || election.getEndDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end dates are required.");
        }

        if (election.getStartDate().isAfter(election.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date.");
        }

        if (election.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be in the future.");
        }

        if ("TOP_TWO_RUNOFF".equalsIgnoreCase(String.valueOf(election.getElectionType()))) {
            if (election.getRunoffStartDate() == null || election.getRunoffEndDate() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Runoff dates are required for TOP_TWO_RUNOFF elections.");
            }

            if (election.getRunoffStartDate().isBefore(election.getEndDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Runoff must start after the main election ends.");
            }

            if (election.getRunoffStartDate().isAfter(election.getRunoffEndDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Runoff start must be before runoff end.");
            }
        }
    }

    public void validateVotesPerVoter(Election election) {
        if (election.getNrVotesPerVoter() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of votes per voter must be specified.");
        }

        if (election.getNrVotesPerVoter() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Votes per voter cannot be negative.");
        }
    }

    @Override
    public void validate(Election election) {
        if (election == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Election cannot be null.");
        }

        validateName(election);
        validateDates(election);
        validateVotesPerVoter(election);
    }
}
